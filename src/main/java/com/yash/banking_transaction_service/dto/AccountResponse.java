package com.yash.banking_transaction_service.dto;

import com.yash.banking_transaction_service.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        String accountNumber,
        String ownerName,
        String email,
        BigDecimal balance,
        Instant createdAt,
        AccountStatus status
) {
}
