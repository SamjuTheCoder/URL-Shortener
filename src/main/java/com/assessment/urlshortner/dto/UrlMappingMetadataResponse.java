package com.assessment.urlshortner.dto;

import java.time.Instant;

/**
 * Author: Julius Fasema
 * Date: 2026-01-28
 * Description: DTO used to return URL metadata information
 *              for the URL shortening service.
 *              Returned by GET /api/urls/{code}
 */
public class UrlMappingMetadataResponse {

    // Shortened URL code
    private String code;

    // Original long URL
    private String longUrl;

    // Fully qualified short URL
    private String shortUrl;

    // Date the URL was created
    private Instant createdAt;

    // Date the URL expires
    private Instant expiresAt;

    // Number of times the URL has been accessed
    private Long hitCount;

    // Indicates whether the URL is expired
    private boolean expired;

    // Getters and Setters
    public String getCode() { 
        return code; 
    }

    public void setCode(String code) { 
        this.code = code; 
    }

    public String getLongUrl() { 
        return longUrl; 
    }

    public void setLongUrl(String longUrl) { 
        this.longUrl = longUrl; 
    }

    public String getShortUrl() { 
        return shortUrl; 
    }

    public void setShortUrl(String shortUrl) { 
        this.shortUrl = shortUrl; 
    }

    public Instant getCreatedAt() { 
        return createdAt; 
    }

    public void setCreatedAt(Instant createdAt) { 
        this.createdAt = createdAt; 
    }

    public Instant getExpiresAt() { 
        return expiresAt; 
    }

    public void setExpiresAt(Instant expiresAt) { 
        this.expiresAt = expiresAt; 
    }

    public Long getHitCount() { 
        return hitCount; 
    }

    public void setHitCount(Long hitCount) { 
        this.hitCount = hitCount; 
    }

    public boolean isExpired() { 
        return expired; 
    }

    public void setExpired(boolean expired) { 
        this.expired = expired; 
    }
}
