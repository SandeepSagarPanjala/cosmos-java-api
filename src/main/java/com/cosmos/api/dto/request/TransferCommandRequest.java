package com.cosmos.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JD 1.7 — Command body for {@code POST /api/transfers} (money movement intent).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferCommandRequest {

    @NotNull(message = "fromAccountId is required")
    private UUID fromAccountId;

    @NotNull(message = "toAccountId is required")
    private UUID toAccountId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    @Size(min = 3, max = 3, message = "currency must be ISO 4217 length 3 (e.g. USD)")
    private String currency;
}
