package com.assessment.urlshortner.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assessment.urlshortner.dto.UrlMappingMetadataResponse;
import com.assessment.urlshortner.dto.UrlMappingRequest;
import com.assessment.urlshortner.dto.UrlMappingResponse;
import com.assessment.urlshortner.exception.UrlExpiredException;
import com.assessment.urlshortner.exception.UrlNotFoundException;
import com.assessment.urlshortner.model.UrlMapping;
import com.assessment.urlshortner.repository.UrlMappingRepository;
import com.assessment.urlshortner.utils.UrlCodeGenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;

/**
 * Author: Julius Fasema
 * Date: 2026-01-28
 * Description: Service layer responsible for URL shortening logic,
 *              redirection handling, metadata retrieval, and cleanup
 *              of expired URLs.
 */
@Service
public class UrlMappingService {

    // Logger for service-level events
    private static final Logger logger = LoggerFactory.getLogger(UrlMappingService.class);

    // Repository for URL mappings
    private final UrlMappingRepository urlMappingRepository;

    // Utility for generating short URL codes
    private final UrlCodeGenerator urlCodeGenerator;

    // Metrics counter for redirects
    private final Counter redirectCounter;

    // Base URL for generating short links
    @Value("${shortener.base-url}")
    private String baseUrl;

    // Length of generated short codes
    @Value("${shortener.code-length:6}")
    private int codeLength;

    // Default expiry duration in days
    @Value("${shortener.default-expiry-days:30}")
    private int defaultExpiryDays;

    // Maximum retry attempts for code collision handling
    @Value("${shortener.max-retries:3}")
    private int maxRetries;

    /**
     * Constructor-based dependency injection.
     */
    public UrlMappingService(
            UrlMappingRepository urlMappingRepository,
            UrlCodeGenerator urlCodeGenerator,
            MeterRegistry meterRegistry) {

        this.urlMappingRepository = urlMappingRepository;
        this.urlCodeGenerator = urlCodeGenerator;
        this.redirectCounter = Counter.builder("shortener.redirect.total")
                .description("Total number of URL redirects")
                .register(meterRegistry);
    }

    /**
     * Creates a new shortened URL or returns an existing one
     * if the long URL already exists and is not expired.
     */
    @Transactional
    public UrlMappingResponse createShortUrl(UrlMappingRequest request) {

        // Check for existing mapping (idempotent behavior)
        Optional<UrlMapping> existing =
                urlMappingRepository.findByLongUrl(request.getLongUrl());

        if (existing.isPresent()) {
            UrlMapping mapping = existing.get();

            // Remove expired mapping and recreate
            if (mapping.isExpired()) {
                urlMappingRepository.delete(mapping);
            } else {
                return toResponse(mapping);
            }
        }

        // Generate a unique short code
        String code = generateUniqueCode();

        // Calculate expiration time
        Instant expiresAt = calculateExpiry(request.getExpiryDays());

        // Persist new URL mapping
        UrlMapping mapping = new UrlMapping(code, request.getLongUrl(), expiresAt);
        mapping = urlMappingRepository.save(mapping);

        logger.info("Created short URL: {} -> {}", code, request.getLongUrl());

        return toResponse(mapping);
    }

    /**
     * Resolves a short code to its original long URL
     * and updates hit count and metrics.
     */
    @Transactional
    public String getLongUrl(String code) {

        // Fetch mapping or fail if not found
        UrlMapping mapping = urlMappingRepository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));

        // Prevent access to expired URLs
        if (mapping.isExpired()) {
            throw new UrlExpiredException("Short URL has expired");
        }

        // Update access statistics
        mapping.incrementHitCount();
        urlMappingRepository.save(mapping);

        // Increment redirect metrics
        redirectCounter.increment();

        logger.debug("Redirecting: {} -> {}", code, mapping.getLongUrl());

        return mapping.getLongUrl();
    }

    /**
     * Retrieves metadata information for a short URL.
     */
    @Transactional(readOnly = true)
    public UrlMappingMetadataResponse getUrlMetadata(String code) {

        UrlMapping mapping = urlMappingRepository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));

        return toMetadataResponse(mapping);
    }

    /**
     * Generates a unique short code with retry-based
     * collision handling.
     */
    private String generateUniqueCode() {
        int attempts = 0;

        while (attempts < maxRetries) {
            String code = urlCodeGenerator.generateCode(codeLength);

            if (!urlMappingRepository.existsByCode(code)) {
                return code;
            }

            attempts++;
            logger.warn("Code collision detected for code: {}, attempt: {}", code, attempts);
        }

        // Fallback: increase code length after max retries
        return urlCodeGenerator.generateCode(codeLength + 1);
    }

    /**
     * Calculates expiration time based on provided or default expiry days.
     */
    private Instant calculateExpiry(Integer expiryDays) {
        if (expiryDays != null && expiryDays > 0) {
            return Instant.now().plusSeconds(expiryDays * 24 * 60 * 60L);
        }
        return Instant.now().plusSeconds(defaultExpiryDays * 24 * 60 * 60L);
    }

    /**
     * Converts UrlMapping entity to a standard response DTO.
     */
    private UrlMappingResponse toResponse(UrlMapping mapping) {
        String shortUrl = baseUrl + "/r/" + mapping.getCode();
        return new UrlMappingResponse(mapping.getCode(), shortUrl);
    }

    /**
     * Converts UrlMapping entity to a metadata response DTO.
     */
    private UrlMappingMetadataResponse toMetadataResponse(UrlMapping mapping) {
        UrlMappingMetadataResponse response = new UrlMappingMetadataResponse();
        response.setCode(mapping.getCode());
        response.setLongUrl(mapping.getLongUrl());
        response.setShortUrl(baseUrl + "/r/" + mapping.getCode());
        response.setCreatedAt(mapping.getCreatedAt());
        response.setExpiresAt(mapping.getExpiresAt());
        response.setHitCount(mapping.getHitCount());
        response.setExpired(mapping.isExpired());
        return response;
    }

    /**
     * Removes expired URLs from the database.
     * Intended for scheduled cleanup tasks.
     */
    @Transactional
    public void cleanupExpiredUrls() {
        long count = urlMappingRepository.countExpiredUrls(Instant.now());
        if (count > 0) {
            urlMappingRepository.deleteExpiredUrls(Instant.now());
            logger.info("Cleaned up {} expired URLs", count);
        }
    }

    private void validateUrl(String longUrl) {
    try {
            new URL(longUrl); // throws MalformedURLException if invalid
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }

    public Object create(Object any) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    public Object resolve(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resolve'");
    }
}
