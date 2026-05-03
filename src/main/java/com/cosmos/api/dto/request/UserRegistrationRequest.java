package com.cosmos.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JD 1.4 — Inbound DTO for {@code POST /api/v1/users}. Describes the JSON contract for <b>registration</b> only.
 * <p>
 * Not the same as {@link com.cosmos.api.entity.User}: never expose persistence fields (e.g. password hash) on the wire;
 * {@code password} here is plain text from the client and mapped to {@code passwordHash} in the service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Size(max = 100, message = "Display name cannot exceed 100 characters")
    private String displayName;
}
