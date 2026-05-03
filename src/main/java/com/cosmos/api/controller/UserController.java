package com.cosmos.api.controller;

import com.cosmos.api.dto.request.UserPatchRequest;
import com.cosmos.api.dto.request.UserRegistrationRequest;
import com.cosmos.api.dto.response.PagedUsersResponse;
import com.cosmos.api.dto.response.UserResponse;
import com.cosmos.api.service.IUserService;
import com.cosmos.api.web.ApiPaths;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for the {@code User} resource.
 * <p>
 * JD 1.1 — Resources &amp; URLs (see {@code learning/point-1-api-design/01-resources-and-urls.md}):
 * <ul>
 *   <li><b>Collection</b> — many users share one path segment: {@code /api/v1/users} (plural noun).</li>
 *   <li><b>Item</b> — one user is {@code /api/v1/users/{id}}; the id lives in the path, not {@code ?id=} for primary lookup.</li>
 *   <li><b>No verbs in URLs</b> — we use HTTP methods ({@code GET}, {@code POST}, …) for the action; the path names the thing.</li>
 *   <li><b>/api/v1</b> — API version in the path (JD 1.8); see {@link ApiPaths}.</li>
 * </ul>
 * <p>
 * JD 1.3 — Status codes &amp; headers: {@code 201} + {@code Location} on create, {@code 204} on delete, {@code 404} for missing user
 * (see {@code learning/point-1-api-design/03-status-codes-and-headers.md}).
 * <p>
 * JD 1.4 — DTOs &amp; validation: request/response types + {@code @Valid} (see {@code learning/point-1-api-design/04-dtos-and-validation.md}).
 * <p>
 * JD 1.6 — List/search/pagination on {@code GET /api/v1/users} (see {@code learning/point-1-api-design/06-list-search-pagination.md}).
 * <p>
 * JD 1.8 — Versioned base path {@link ApiPaths#V1_USERS}.
 */
@Validated
@RestController
@RequestMapping(ApiPaths.V1_USERS)
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * Creates a new user in the <b>collection</b> {@code /api/v1/users}.
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
     * Retrieves a single <b>item resource</b>: {@code GET /api/v1/users/{id}}.
     * {@code {id}} is a path variable — the canonical place for the resource identifier in REST APIs.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Partial update — {@code PATCH /api/v1/users/{id}} with a {@link UserPatchRequest} (different shape from full registration).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok(userService.patchUser(id, request));
    }

    /**
     * Paginated list of the collection — {@code GET /api/v1/users?page=&amp;size=&amp;email=&amp;sortBy=&amp;sortDir=}.
     * <p>
     * Query params (JD 1.6): optional {@code email} substring search; {@code page} (0-based); {@code size} (capped);
     * {@code sortBy} whitelist ({@code createdAt}, {@code email}, {@code username}); {@code sortDir} {@code asc} or {@code desc}.
     */
    @GetMapping
    public ResponseEntity<PagedUsersResponse> listUsers(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(userService.searchUsers(email, page, size, sortBy, sortDir));
    }

    /**
     * Deletes the item {@code /api/v1/users/{id}}. Same path as GET by id — different HTTP method (JD 1.2).
     * Returns {@code 204 No Content} — success with no body (typical for DELETE).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
