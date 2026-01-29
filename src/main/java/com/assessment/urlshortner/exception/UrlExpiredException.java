package com.assessment.urlshortner.exception;

import org.springframework.http.HttpStatus;

public class UrlExpiredException extends ApiException {
    public UrlExpiredException(String message) {
        super(message, HttpStatus.GONE);
    }
}
