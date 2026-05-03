package com.cosmos.api.controller;

import com.cosmos.api.dto.request.UserPatchRequest;
import com.cosmos.api.dto.request.UserRegistrationRequest;
import com.cosmos.api.dto.response.UserResponse;
import com.cosmos.api.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for the {@code User} resource.
 * <p>
 * JD 1.1 — Resources &amp; URLs (see {@code learning/point-1-api-design/01-resources-and-urls.md}):
 * <ul>
 *   <li><b>Collection</b> — many users share one path segment: {@code /api/users} (plural noun).</li>
 *   <li><b>Item</b> — one user is {@code /api/users/{id}}; the id lives in the path, not {@code ?id=} for primary lookup.</li>
 *   <li><b>No verbs in URLs</b> — we use HTTP methods ({@code GET}, {@code POST}, …) for the action; the path names the thing.</li>
 *   <li><b>/api</b> — separates this JSON API from other web routes; version segment ({@code /v1}) can be added later (JD 1.8).</li>
 * </ul>
 * <p>
 * JD 1.3 — Status codes &amp; headers: {@code 201} + {@code Location} on create, {@code 204} on delete, {@code 404} for missing user
 * (see {@code learning/point-1-api-design/03-status-codes-and-headers.md}).
 * <p>
 * JD 1.4 — DTOs &amp; validation: request/response types + {@code @Valid} (see {@code learning/point-1-api-design/04-dtos-and-validation.md}).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * Creates a new user in the <b>collection</b> {@code /api/users}.
     * <p>
     * {@code POST} on the collection URI is the usual pattern for “create a new member” (details: JD 1.2).
     * The body carries the representation to store ({@link UserRegistrationRequest}), not the URL.
     * <p>
     * Returns {@code 201 Created} and a {@code Location} header pointing at the new item URI (RFC 9110 / common REST practice).
     * {@code @Valid} runs Bean Validation on {@link UserRegistrationRequest}; failures become {@code 400} via {@code GlobalExceptionHandler}.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse created = userService.createUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a single <b>item resource</b>: {@code GET /api/users/{id}}.
     * {@code {id}} is a path variable — the canonical place for the resource identifier in REST APIs.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Partial update — {@code PATCH /api/users/{id}} with a {@link UserPatchRequest} (different shape from full registration).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok(userService.patchUser(id, request));
    }

    /**
     * Lists the collection — {@code GET /api/users} with no extra path after the plural segment.
     * <p>
     * Filtering/pagination (e.g. {@code ?page=0&amp;size=20}) belong here as <b>query parameters</b> (JD 1.6), not as {@code /getAllUsers}.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Deletes the item {@code /api/users/{id}}. Same path as GET by id — different HTTP method (JD 1.2).
     * Returns {@code 204 No Content} — success with no body (typical for DELETE).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
