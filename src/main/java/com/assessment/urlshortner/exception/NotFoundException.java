package com.assessment.urlshortner.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 *
 * This exception is handled globally and translated
 * into an HTTP 404 response using Problem Details.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Creates a new NotFoundException with a descriptive message.
     *
     * @param message explanation of the missing resource
     */
    public NotFoundException(String message) {
        super(message);
    }
}
