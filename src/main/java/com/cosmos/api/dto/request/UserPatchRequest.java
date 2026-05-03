package com.cosmos.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JD 1.4 — Partial update body for {@code PATCH /api/v1/users/{id}}.
 * <p>
 * Fields are optional: omitted or {@code null} means “do not change.”
 * When a field is present, Bean Validation runs on it before the service layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchRequest {

    @Size(max = 100, message = "Display name cannot exceed 100 characters")
    private String displayName;
}
