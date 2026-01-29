package com.assessment.urlshortner.dto;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Author: Julius Fasema
 * Date: 2026-01-28
 * Description: Request DTO used for creating a shortened URL.
 *              Accepted by POST /api/urls
 */
public class UrlMappingRequest {

    // Original URL to be shortened
    @NotBlank(message = "URL is required")
    @Size(max = 2048, message = "URL must be less than 2048 characters")
    @URL(message = "Invalid URL format")
    @Pattern(
        regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
        message = "Invalid URL format"
    )
    private String longUrl;

    // Number of days before the URL expires (internal use only)
    @Hidden
    private Integer expiryDays;

    // Getters and Setters
    public String getLongUrl() { 
        return longUrl; 
    }

    public void setLongUrl(String longUrl) { 
        this.longUrl = longUrl; 
    }

    public Integer getExpiryDays() { 
        return expiryDays; 
    }

    public void setExpiryDays(Integer expiryDays) { 
        this.expiryDays = expiryDays; 
    }
}
