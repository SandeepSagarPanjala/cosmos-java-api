package com.cosmos.api.exception;

/**
 * Transfer command breaks domain rules (e.g. same from/to) — HTTP 400 in {@link GlobalExceptionHandler}.
 */
public class InvalidTransferRequestException extends RuntimeException {

    public InvalidTransferRequestException(String message) {
        super(message);
    }
}
