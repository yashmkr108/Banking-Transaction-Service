package com.yash.banking_transaction_service.controller;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.service.TransferOrchestrator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferOrchestrator transferOrchestrator;

    public TransferController(TransferOrchestrator transferOrchestrator) {
        this.transferOrchestrator = transferOrchestrator;
    }

    @PostMapping
    public TransferResponse transfer(
            @RequestHeader("Idempotency-Key") @NotBlank @Size(max = 100) String idempotencyKey
            , @Valid @RequestBody CreateTransferRequest request
    ) {
        return transferOrchestrator.createAndExecute(idempotencyKey, request);
    }

}
