package com.assessment.urlshortner.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.assessment.urlshortner.model.UrlMapping;
import com.assessment.urlshortner.service.UrlMappingService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Controller responsible for resolving short URL codes
 * and redirecting clients to the original long URL.
 */
@RestController
public class UrlRedirectController {

    // Service handling URL resolution and hit counting
    private final UrlMappingService urlMappingService;

    // Micrometer counter for tracking redirect events
    private final Counter redirectCounter;

    /**
     * Constructor-based dependency injection.
     *
     * @param urlMappingService service responsible for resolving short URLs
     * @param registry Micrometer registry used to register custom metrics
     */
    public UrlRedirectController(UrlMappingService urlMappingService, MeterRegistry registry) {
        this.urlMappingService = urlMappingService;

        // Custom metric exposed via Actuator:
        // shortener_redirect_total
        this.redirectCounter = registry.counter("shortener_redirect_total");
    }

    /**
     * Resolves a short URL code and redirects to the original long URL.
     *
     * Endpoint: GET /r/{code}
     *
     * Behaviour:
     *  - Valid code → HTTP 302 redirect
     *  - Invalid or expired code → 404 (handled globally)
     *
     * @param code short URL identifier
     * @return HTTP 302 response with Location header set
     */
    @GetMapping("/r/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {

        // Resolve the short code to the original URL
        UrlMapping mapping = urlMappingService.resolve(code);

        // Increment custom redirect counter metric
        redirectCounter.increment();

        // Return HTTP 302 Found with Location header
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(mapping.getLongUrl()))
                .build();
    }
}
