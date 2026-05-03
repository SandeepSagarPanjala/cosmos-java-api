package com.cosmos.api.exception;

import java.net.URI;

/**
 * Stable {@code type} URIs for {@link org.springframework.http.ProblemDetail} (RFC 7807).
 * In production these often resolve to human-readable documentation.
 */
public final class ProblemTypeUri {

    public static final URI USER_NOT_FOUND = URI.create("https://api.cosmos.local/problems/user-not-found");
    public static final URI VALIDATION_FAILED = URI.create("https://api.cosmos.local/problems/validation-failed");

    public static final URI IDEMPOTENCY_CONFLICT = URI.create("https://api.cosmos.local/problems/idempotency-key-conflict");

    public static final URI INVALID_TRANSFER = URI.create("https://api.cosmos.local/problems/invalid-transfer");

    public static final URI TRANSFER_NOT_FOUND = URI.create("https://api.cosmos.local/problems/transfer-not-found");

    /** Catch-all unexpected failures mapped to HTTP 500 (do not leak internals in {@code detail}). */
    public static final URI INTERNAL_ERROR = URI.create("https://api.cosmos.local/problems/internal-error");

    private ProblemTypeUri() {
    }
}
