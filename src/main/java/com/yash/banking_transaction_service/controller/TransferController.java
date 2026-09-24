package com.yash.banking_transaction_service.controller;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.orchestration.TransferOrchestrator;
import com.yash.banking_transaction_service.service.TransferService;
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
    private final TransferService transferService;

    public TransferController(TransferOrchestrator transferOrchestrator, TransferService transferService) {
        this.transferOrchestrator = transferOrchestrator;
        this.transferService = transferService;
    }

    @PostMapping
    public TransferResponse transfer(
            @RequestHeader("Idempotency-Key") @NotBlank @Size(max = 100) String idempotencyKey
            , @Valid @RequestBody CreateTransferRequest request
    ) {
        return transferOrchestrator.createAndExecute(idempotencyKey, request);
    }

    @PostMapping("/{reference}/execute")
    public TransferResponse execute(@PathVariable String reference) {
        return transferService.executeTransfer(reference);
    }

}
