package com.cosmos.api.sample;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Learning-only controller for JD 1.2.
 * <p>
 * This class is intentionally simple and in-memory so you can focus on HTTP semantics:
 * method meaning, safe vs idempotent behavior, and idempotency-key patterns.
 */
@RestController
@RequestMapping("/api/learning/http-methods")
public class HttpMethodSemanticsDemoController {

    /**
     * Tiny in-memory stores only for learning.
     */
    private final Map<UUID, Map<String, Object>> users = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> transferResultsByIdempotencyKey = new ConcurrentHashMap<>();

    /**
     * GET is safe + idempotent: reading state does not modify business data.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable UUID id) {
        Map<String, Object> user = users.get(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found", "id", id));
        }
        return ResponseEntity.ok(user);
    }

    /**
     * POST is usually NOT idempotent for creates: repeated request can create new records.
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> request) {
        UUID id = UUID.randomUUID();
        Map<String, Object> saved = new HashMap<>(request);
        saved.put("id", id);
        saved.put("createdAt", OffsetDateTime.now());
        users.put(id, saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT is idempotent for full replacement.
     * Calling this repeatedly with the same payload leads to the same final state.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> replaceUser(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> replaced = new HashMap<>();
        replaced.put("id", id);
        replaced.put("email", request.get("email"));
        replaced.put("username", request.get("username"));
        replaced.put("displayName", request.get("displayName"));
        replaced.put("updatedAt", OffsetDateTime.now());
        users.put(id, replaced);
        return ResponseEntity.ok(replaced);
    }

    /**
     * PATCH is partial update; this design is idempotent for the same field/value payload.
     */
    @PatchMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> patchUser(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> patch) {
        Map<String, Object> existing = users.computeIfAbsent(id, ignored -> {
            Map<String, Object> base = new HashMap<>();
            base.put("id", id);
            return base;
        });
        existing.putAll(patch);
        existing.put("updatedAt", OffsetDateTime.now());
        return ResponseEntity.ok(existing);
    }

    /**
     * DELETE is idempotent: after first success, repeating keeps final state as "deleted".
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        users.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Idempotency-key demo for command-like POST (e.g. create transfer).
     * <p>
     * Same key + same intent => return previously stored result instead of executing again.
     */
    @PostMapping("/transfers")
    public ResponseEntity<Map<String, Object>> createTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody Map<String, Object> request) {
        if (transferResultsByIdempotencyKey.containsKey(idempotencyKey)) {
            return ResponseEntity.ok(transferResultsByIdempotencyKey.get(idempotencyKey));
        }

        Map<String, Object> transfer = new HashMap<>();
        transfer.put("transferId", UUID.randomUUID());
        transfer.put("status", "ACCEPTED");
        transfer.put("amount", request.get("amount"));
        transfer.put("fromAccountId", request.get("fromAccountId"));
        transfer.put("toAccountId", request.get("toAccountId"));
        transfer.put("createdAt", OffsetDateTime.now());

        transferResultsByIdempotencyKey.put(idempotencyKey, transfer);
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }
}
