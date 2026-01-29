package com.assessment.urlshortner.dto;

/**
 * Author: Julius Fasema Senior
 * Date: 2026-01-28
 * Description: Response DTO returned after successfully
 *              creating a shortened URL.
 */
public class UrlMappingResponse {

    // Generated short code for the URL
    private String code;

    // Fully qualified shortened URL
    private String shortUrl;

    // Constructor
    public UrlMappingResponse(String code, String shortUrl) {
        this.code = code;
        this.shortUrl = shortUrl;
    }

    // Getters and Setters
    public String getCode() { 
        return code; 
    }

    public void setCode(String code) { 
        this.code = code; 
    }

    public String getShortUrl() { 
        return shortUrl; 
    }

    public void setShortUrl(String shortUrl) { 
        this.shortUrl = shortUrl; 
    }
}
