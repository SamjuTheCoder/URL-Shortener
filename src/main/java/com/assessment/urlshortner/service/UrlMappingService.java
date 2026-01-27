package com.assessment.urlshortner.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assessment.urlshortner.exception.NotFoundException;
import com.assessment.urlshortner.model.UrlMapping;
import com.assessment.urlshortner.repository.UrlMappingRepository;

/**
 * Service layer containing business logic for URL mapping operations.
 *
 * Responsibilities:
 * - Create new short URLs
 * - Resolve short codes to long URLs
 * - Enforce idempotency and expiry rules
 * - Maintain hit counters
 */

@Service
@Transactional
public class UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;
    private final UrlCodeGenerator urlCodeGenerator;

     /**
     * Constructor-based dependency injection.
     *
     * @param urlMappingRepository repository for persistence operations
     * @param urlCodeGenerator     component responsible for generating short codes
     */
    public UrlMappingService(UrlMappingRepository urlMappingRepository, UrlCodeGenerator urlCodeGenerator){
        this.urlMappingRepository = urlMappingRepository;
        this.urlCodeGenerator = urlCodeGenerator;
    }

    /**
     * Creates a new URL mapping for the given long URL.
     *
     * Idempotent behavior:
     * - If the long URL already exists, the existing mapping is returned.
     * - Otherwise, a new short code is generated and persisted.
     *
     * Record exists handling:
     * - Short code generation is retried until a unique code is found.
     *
     * @param longUrl the original URL to be shortened
     * @return persisted UrlMapping entity
     */
    public UrlMapping create(String longUrl) {

        return urlMappingRepository.findByLongUrl(longUrl)
            .orElseGet(() -> {
                UrlMapping mapping = new UrlMapping();
                mapping.setLongUrl(longUrl);
                mapping.setCreatedAt(Instant.now());

                 // Generate a unique short code (retry if exists)
                String code;
                do {
                    code = urlCodeGenerator.generate();
                } while (urlMappingRepository.existsByCode(code));

                mapping.setCode(code);
                return urlMappingRepository.save(mapping);
            });

    }

    /**
     * Resolves a short code to its corresponding URL mapping.
     *
     * Business rules:
     * - Throws NotFoundException if the code does not exist
     * - Throws NotFoundException if the URL has expired
     * - Increments hit counter on successful resolution
     *
     * @param code the short URL code
     * @return UrlMapping entity
     */
    public UrlMapping resolve(String code) {
        UrlMapping mapping = urlMappingRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Short URL not found"));

        // Check for expiration (if expiry is configured)
        if (mapping.getExpiresAt() != null &&
            mapping.getExpiresAt().isBefore(Instant.now())) {
            throw new NotFoundException("Short URL expired");
        }

        // Increment hit counter for analytics/metrics
        mapping.setHitCount(mapping.getHitCount() + 1);
        return mapping;
    }

    /**
     * Retrieves metadata for a given short code without modifying state.
     *
     * Read-only transaction improves performance and
     * prevents accidental writes.
     *
     * @param code the short URL code
     * @return UrlMapping entity
     */
    @Transactional(readOnly = true)
    public UrlMapping getMetadata(String code) {
        return urlMappingRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Short URL not found"));
    }
    
}
