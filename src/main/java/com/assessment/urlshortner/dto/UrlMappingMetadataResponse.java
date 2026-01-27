package com.assessment.urlshortner.dto;

import java.time.Instant;

/**
 * Response DTO for URL metadata endpoint.
 * Returned by GET /api/urls/{code}
 */
public class UrlMappingMetadataResponse {

    // Short code (e.g. abc123)
    private String code;

    // Original long URL
    private String longUrl;

    // When the short URL was created
    private Instant createdAt;

    // Optional expiration timestamp
    private Instant expiresAt;

    // Number of times the short URL was accessed
    private long hitCount;

    public UrlMappingMetadataResponse(
            String code,
            String longUrl,
            Instant createdAt,
            Instant expiresAt,
            long hitCount
    ) {
        this.code = code;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.hitCount = hitCount;
    }

    public String getCode() {
        return code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public long getHitCount() {
        return hitCount;
    }
}
