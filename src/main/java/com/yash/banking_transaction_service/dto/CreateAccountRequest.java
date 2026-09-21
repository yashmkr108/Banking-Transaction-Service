package com.yash.banking_transaction_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank
        @Size(max = 150)
        String ownerName,

        @NotBlank
        @Email
        String email
) {
}
