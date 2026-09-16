package com.yash.banking_transaction_service.dto;

import com.yash.banking_transaction_service.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
        String reference,
        String sourceAccountNumber,
        String destinationAccountNumber,
        BigDecimal amount,
        TransferStatus status,
        Instant createdAt,
        Instant updatedAt)
{
}
