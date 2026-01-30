package com.assessment.urlshortner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.assessment.urlshortner.dto.UrlMappingMetadataResponse;
import com.assessment.urlshortner.dto.UrlMappingRequest;
import com.assessment.urlshortner.dto.UrlMappingResponse;
import com.assessment.urlshortner.service.UrlMappingService;

import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Julius Fasema
 * Date: 2026-01-28
 * Description: REST controller responsible for handling
 *              URL shortening requests and metadata retrieval.
 */
@RestController
@RequestMapping("/api/urls")
@Tag(name = "URL Shortener", description = "URL Shortener API")
public class UrlMappingController {

    private static final Logger logger = LoggerFactory.getLogger(UrlMappingController.class);

    private final UrlMappingService urlMappingService;

    public UrlMappingController(UrlMappingService urlMappingService) {
        this.urlMappingService = urlMappingService;
    }

    @PostMapping
    @Operation(
            summary = "Create a short URL",
            description = "Creates a shortened version of a long URL"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Short URL created successfully",
                    content = @Content(schema = @Schema(implementation = UrlMappingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - URL format is incorrect"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests - rate limit exceeded"
            )
    })
    public ResponseEntity<UrlMappingResponse> createShortUrl(
            @Valid @RequestBody UrlMappingRequest request, HttpServletRequest http) {

        // Validate empty or null URL
        if (request.getLongUrl() == null || request.getLongUrl().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Validate URL format
        if (!isValidUrl(request.getLongUrl())) {
            return ResponseEntity.badRequest().build();
        }

        // Delegate to service layer
        UrlMappingResponse response = urlMappingService.createShortUrl(request);

        // Log the creation
        logger.info("Created short URL for long URL {} from IP {}",
        request.getLongUrl(), http.getRemoteAddr());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{code}")
    @Operation(
            summary = "Get URL metadata",
            description = "Retrieves metadata for a given short URL code"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL metadata retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UrlMappingMetadataResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found - invalid or expired code"
            )
    })
    public ResponseEntity<UrlMappingMetadataResponse> getUrlMetadata(
            @Parameter(
                    description = "Short URL code - the unique identifier after the domain",
                    example = "samju1234"
            )
            @PathVariable String code) {

        UrlMappingMetadataResponse metadata =
                urlMappingService.getUrlMetadata(code);

        return ResponseEntity.ok(metadata);
    }

    /**
     * Simple URL format validation using java.net.URL
     */
    private boolean isValidUrl(String url) {
        try {
            if (url == null || url.isBlank()) return false;
            URL u = new URL(url);
            String protocol = u.getProtocol();
            return protocol.equals("http") || protocol.equals("https") || protocol.equals("ftp");
        } catch (Exception e) {
            return false;
        }
    }
    
}
