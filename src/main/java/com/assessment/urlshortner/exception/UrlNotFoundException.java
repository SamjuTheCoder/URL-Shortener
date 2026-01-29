package com.assessment.urlshortner.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource cannot be found.
 *
 * This exception is handled globally and translated
 * into an HTTP 404 response using Problem Details.
 */
public class UrlNotFoundException extends ApiException {

    /**
     * Creates a new NotFoundException with a descriptive message.
     *
     * @param message explanation of the missing resource
     */
    public UrlNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
