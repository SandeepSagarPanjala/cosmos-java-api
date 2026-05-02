package com.cosmos.api.annotation.custom;

/**
 * Used as the type of an annotation member — shows annotations can use enums, not only strings/primitives.
 */
public enum DataClassification {
    PUBLIC,
    INTERNAL,
    RESTRICTED
}
