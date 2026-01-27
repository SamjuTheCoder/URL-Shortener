package com.assessment.urlshortner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assessment.urlshortner.dto.CreateUrlMappingRequest;
import com.assessment.urlshortner.dto.CreateUrlMappingResponse;
import com.assessment.urlshortner.dto.UrlMappingMetadataResponse;
import com.assessment.urlshortner.model.UrlMapping;
import com.assessment.urlshortner.service.UrlMappingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * REST controller responsible for managing URL shortening operations.
 *
 * Exposes endpoints to:
 *  - Create a short URL
 *  - Retrieve metadata about a short URL
 */
@RestController
@RequestMapping("/api/urls")
public class UrlMappingController {

    // Service layer handling business logic
    private final UrlMappingService urlMappingService;

    /**
     * Constructor-based dependency injection.
     * Spring automatically injects the UrlMappingService bean.
     */
    public UrlMappingController(UrlMappingService urlMappingService) {
        this.urlMappingService = urlMappingService;
    }

    /**
     * Creates a new short URL for a given long URL.
     *
     * Endpoint: POST /api/urls
     * Request body is validated using Jakarta Bean Validation.
     *
     * @param request incoming request containing the long URL
     * @param http HttpServletRequest used to construct the base URL dynamically
     * @return short code and fully-qualified short URL
     */
    @PostMapping
    public ResponseEntity<CreateUrlMappingResponse> create(
            @Valid @RequestBody CreateUrlMappingRequest request,
            HttpServletRequest http
    ) {
        // Delegate creation logic to service layer
        UrlMapping mapping = urlMappingService.create(request.longUrl());

        // Dynamically build the base URL (scheme + host + port)
        String baseUrl = http.getScheme() + "://" +
                http.getServerName() + ":" +
                http.getServerPort();

        // Return response DTO (not entity) to keep API decoupled
        return ResponseEntity.ok(
                new CreateUrlMappingResponse(
                        mapping.getCode(),
                        baseUrl + "/r/" + mapping.getCode()
                )
        );
    }

    /**
     * Retrieves metadata for a short URL without redirecting.
     *
     * Endpoint: GET /api/urls/{code}
     *
     * @param code short URL identifier
     * @return metadata including creation time and hit count
     */
    @GetMapping("/{code}")
    public UrlMappingMetadataResponse metadata(@PathVariable String code) {
        // Fetch metadata from service layer
        UrlMapping m = urlMappingService.getMetadata(code);

        // Map entity to response DTO
        return new UrlMappingMetadataResponse(
                m.getCode(),
                m.getLongUrl(),
                m.getCreatedAt(),
                m.getExpiresAt(),
                m.getHitCount()
        );
    }
}
