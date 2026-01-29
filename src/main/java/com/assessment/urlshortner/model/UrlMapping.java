package com.assessment.urlshortner.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Entity representing a shortened URL mapping.
 *
 * Stores the generated short code, the original long URL,
 * creation metadata, optional expiry, and access statistics.
 */

@Entity
@Table(
    name = "url_mappings",
    indexes = {
        @Index(name = "idx_code", columnList = "code", unique = true),
        @Index(name = "idx_long_url", columnList = "longUrl"),
        @Index(name = "idx_expires_at", columnList = "expiresAt")
    }
)
public class UrlMapping {

    /**
     * Primary key.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short URL code (Base62).
     * Unique and indexed for fast resolution.
     */
    @Column(nullable = false, unique = true, length = 16)
    private String code;

    /**
     * Original long URL.
     * Length capped to prevent excessive payloads.
     */
    @Column(nullable = false, length = 2048)
    private String longUrl;

    /**
     * Timestamp when the short URL was created.
     */
    @Column(nullable = false)
    private Instant createdAt;

    /**
     * Optional expiration timestamp.
     * If set and in the past, the URL should be treated as invalid.
     */
    private Instant expiresAt;

    /**
     * Number of times the short URL has been successfully resolved.
     */
    @Column(nullable = false)
    private long hitCount = 0L;

    // Constructors
    public UrlMapping() {
        // Default constructor for JPA
    }

    public UrlMapping(String code, String longUrl, Instant expiresAt) {
        this.code = code;
        this.longUrl = longUrl;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now(); 
        this.hitCount = 0;
    }

    // --------------------
    // Getters and Setters
    // --------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public void incrementHitCount() {
        this.hitCount++;
    }
    
    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
    
}

