package com.yash.banking_transaction_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateTransferRequest(

        @NotBlank(message = "Source account number is required")
        String sourceAccountNumber,

        @NotBlank(
                message = "Destination account number is required"
        )
        String destinationAccountNumber,

        @DecimalMin(
                value = "0.01",
                message = "Transfer amount must be at least 0.01"
        )
        @Digits(
                integer = 17,
                fraction = 2,
                message = "Amount must have at most 2 decimal places"
        )
        BigDecimal amount){}