package com.cosmos.api.exception;

import java.net.URI;

/**
 * Stable {@code type} URIs for {@link org.springframework.http.ProblemDetail} (RFC 7807).
 * In production these often resolve to human-readable documentation.
 */
public final class ProblemTypeUri {

    public static final URI USER_NOT_FOUND = URI.create("https://api.cosmos.local/problems/user-not-found");
    public static final URI VALIDATION_FAILED = URI.create("https://api.cosmos.local/problems/validation-failed");

    private ProblemTypeUri() {
    }
}
