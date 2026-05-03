package com.cosmos.api.controller;

import com.cosmos.api.dto.request.TransferCommandRequest;
import com.cosmos.api.dto.response.TransferResponse;
import com.cosmos.api.service.ITransferService;
import com.cosmos.api.web.ApiPaths;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * JD 1.7 — Domain <b>command</b> as its own resource: {@code POST /api/v1/transfers}.
 * <p>
 * JD 1.8 — Versioned base path {@link ApiPaths#V1_TRANSFERS}.
 * <p>
 * Not modeled as {@code POST /accounts/{id}/subtract} — the transfer is the aggregate boundary for this use case.
 */
@Validated
@RestController
@RequestMapping(ApiPaths.V1_TRANSFERS)
@RequiredArgsConstructor
public class TransferController {

    private final ITransferService transferService;

    /**
     * Submits a transfer. Requires {@code Idempotency-Key} so retries do not double-post (pairs with JD 1.2).
     */
    @PostMapping
    public ResponseEntity<TransferResponse> submitTransfer(
            @RequestHeader("Idempotency-Key") @NotBlank(message = "Idempotency-Key header is required") String idempotencyKey,
            @Valid @RequestBody TransferCommandRequest request) {
        TransferResponse body = transferService.submitTransfer(idempotencyKey.trim(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(body.getTransferId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> getTransfer(@PathVariable UUID id) {
        return ResponseEntity.ok(transferService.getTransfer(id));
    }
}
