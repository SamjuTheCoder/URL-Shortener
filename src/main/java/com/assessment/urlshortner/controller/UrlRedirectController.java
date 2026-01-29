package com.assessment.urlshortner.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assessment.urlshortner.service.UrlMappingService;

/**
 * Author: Julius Fasema
 * Date: 2026-01-28
 * Description: Controller responsible for handling short URL
 *              redirection requests. Resolves a short code
 *              and redirects clients to the original URL.
 */
@RestController
// Base path used for redirection endpoints
@RequestMapping("/r")
public class UrlRedirectController {

    // Service layer dependency
    private final UrlMappingService urlService;
    private final Counter redirectCounter;

    // Constructor-based dependency injection
    public UrlRedirectController(UrlMappingService urlService, MeterRegistry registry) {
        this.urlService = urlService;
        this.redirectCounter = registry.counter("shortener_redirect_total");
    }

    /**
     * Redirects a short URL code to its original long URL.
     * Endpoint: GET /r/{code}
     *
     * @param code Short URL code
     * @return HTTP 302 Found with Location header set
     */
    @GetMapping("/{code}")
    @Operation(
            summary = "Redirect to original URL",
            description = "Returns HTTP 302 redirect to the original URL. " +
                          "This endpoint is intended for browsers or HTTP clients " +
                          "that automatically follow redirects.",
            tags = {"Redirect"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to original URL",
                    headers = @Header(
                            name = "Location",
                            description = "The original long URL",
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = org.springframework.http.ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Short URL has expired",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = org.springframework.http.ProblemDetail.class
                            )
                    )
            )
    })
    public ResponseEntity<Void> redirect(
            @Parameter(
                    description = "Short URL code",
                    example = "samju1234",
                    required = true
            )
            @PathVariable String code) {

        // Resolve the short code to its original URL
        String longUrl = urlService.getLongUrl(code);

        // Increment the custom counter every time a redirect occurs
        redirectCounter.increment();

        // Return HTTP 302 Found with Location header
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }
}
