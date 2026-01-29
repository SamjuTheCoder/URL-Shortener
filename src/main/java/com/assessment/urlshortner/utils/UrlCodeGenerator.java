package com.assessment.urlshortner.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Set;

/**
 * Author: Julius Fasema
 * Date: 2026-01-28
 * Description: Utility class responsible for generating
 *              random and unique Base62 URL short codes.
 */
@Component
public class UrlCodeGenerator {

    // Characters allowed in short URL codes (Base62)
    private static final String BASE62 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Default length of generated short codes
    private static final int DEFAULT_LENGTH = 6;

    // Secure random generator for better randomness
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random short code using the default length.
     */
    public String generateCode() {
        return generateCode(DEFAULT_LENGTH);
    }

    /**
     * Generates a random short code of a specified length.
     */
    public String generateCode(int length) {
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }

        return code.toString();
    }

    /**
     * Generates a unique short code using the default length,
     * ensuring it does not exist in the provided set.
     */
    public String generateUniqueCode(Set<String> existingCodes) {
        return generateUniqueCode(DEFAULT_LENGTH, existingCodes);
    }

    /**
     * Generates a unique short code of a given length.
     * Automatically increases length after repeated collisions.
     */
    public String generateUniqueCode(int length, Set<String> existingCodes) {
        String code;
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;

        do {
            code = generateCode(length);
            attempts++;

            // Increase code length after too many collisions
            if (attempts >= MAX_ATTEMPTS) {
                length++;
                attempts = 0;
            }

        } while (existingCodes.contains(code));

        return code;
    }

    /**
     * Validates whether a given code is a valid Base62 short code.
     */
    public boolean isValidCode(String code) {
        if (code == null || code.length() < 3 || code.length() > 10) {
            return false;
        }

        for (char c : code.toCharArray()) {
            if (BASE62.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }
}
