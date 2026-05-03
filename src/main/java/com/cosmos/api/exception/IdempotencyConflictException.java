package com.cosmos.api.exception;

/**
 * Same {@code Idempotency-Key} was reused with a different command body — HTTP 409 in {@link GlobalExceptionHandler}.
 */
public class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String message) {
        super(message);
    }
}
