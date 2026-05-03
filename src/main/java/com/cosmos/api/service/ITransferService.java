package com.cosmos.api.service;

import com.cosmos.api.dto.request.TransferCommandRequest;
import com.cosmos.api.dto.response.TransferResponse;

import java.util.UUID;

public interface ITransferService {

    TransferResponse submitTransfer(String idempotencyKey, TransferCommandRequest request);

    TransferResponse getTransfer(UUID id);
}
