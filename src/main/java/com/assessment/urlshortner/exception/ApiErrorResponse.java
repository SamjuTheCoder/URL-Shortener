package com.assessment.urlshortner.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public class ApiErrorResponse {
    private String title;
    private int status;
    private String message;
    private String instance;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant timestamp;
    
    // Constructors
    public ApiErrorResponse() {}
    
    public ApiErrorResponse(String title, int status, String message, String instance) {
        this.title = title;
        this.status = status;
        this.message = message;
        this.instance = instance;
        this.timestamp = Instant.now();
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getInstance() {
        return instance;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    
}
