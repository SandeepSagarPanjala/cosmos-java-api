package com.cosmos.api.exception;

/**
 * Thrown when a requested user does not exist. Mapped to HTTP 404 in {@link GlobalExceptionHandler}.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
