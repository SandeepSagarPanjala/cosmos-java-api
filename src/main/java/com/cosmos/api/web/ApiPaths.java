package com.cosmos.api.web;

/**
 * Public HTTP path prefixes (JD 1.8 — URI versioning). Controllers reference these so the version lives in one place.
 */
public final class ApiPaths {

    public static final String V1_USERS = "/api/v1/users";
    public static final String V1_TRANSFERS = "/api/v1/transfers";

    private ApiPaths() {
    }
}
