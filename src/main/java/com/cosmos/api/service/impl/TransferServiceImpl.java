package com.cosmos.api.service.impl;

import com.cosmos.api.dto.request.TransferCommandRequest;
import com.cosmos.api.dto.response.TransferResponse;
import com.cosmos.api.entity.Transfer;
import com.cosmos.api.exception.IdempotencyConflictException;
import com.cosmos.api.exception.InvalidTransferRequestException;
import com.cosmos.api.exception.TransferNotFoundException;
import com.cosmos.api.repository.TransferRepository;
import com.cosmos.api.service.ITransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements ITransferService {

    private final TransferRepository transferRepository;

    @Override
    @Transactional
    public TransferResponse submitTransfer(String idempotencyKey, TransferCommandRequest request) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new InvalidTransferRequestException("fromAccountId and toAccountId must differ.");
        }

        String currency = request.getCurrency().trim().toUpperCase();

        Optional<Transfer> existing = transferRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            if (sameCommand(existing.get(), request, currency)) {
                return mapToResponse(existing.get());
            }
            throw new IdempotencyConflictException(
                    "Idempotency-Key was already used with a different transfer payload.");
        }

        Transfer saved = transferRepository.save(Transfer.builder()
                .idempotencyKey(idempotencyKey)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .currency(currency)
                .status("ACCEPTED")
                .build());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferResponse getTransfer(UUID id) {
        return transferRepository.findById(id)
                .map(TransferServiceImpl::mapToResponse)
                .orElseThrow(() -> new TransferNotFoundException(id));
    }

    private static boolean sameCommand(Transfer stored, TransferCommandRequest request, String normalizedCurrency) {
        return stored.getFromAccountId().equals(request.getFromAccountId())
                && stored.getToAccountId().equals(request.getToAccountId())
                && stored.getAmount().compareTo(request.getAmount()) == 0
                && stored.getCurrency().equals(normalizedCurrency);
    }

    private static TransferResponse mapToResponse(Transfer t) {
        return TransferResponse.builder()
                .transferId(t.getId())
                .fromAccountId(t.getFromAccountId())
                .toAccountId(t.getToAccountId())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
