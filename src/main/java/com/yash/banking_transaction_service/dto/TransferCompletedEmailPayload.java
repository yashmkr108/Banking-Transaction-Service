package com.yash.banking_transaction_service.dto;

import java.math.BigDecimal;

public record TransferCompletedEmailPayload(
        String transferReference,
        String destinationAccountNumber,
        String destinationEmail,
        BigDecimal amount
) {
}