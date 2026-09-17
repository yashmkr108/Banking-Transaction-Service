package com.yash.banking_transaction_service.controller;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.service.TransferOrchestrator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferOrchestrator transferOrchestrator;

    public TransferController(TransferOrchestrator transferOrchestrator) {
        this.transferOrchestrator = transferOrchestrator;
    }

    @PostMapping
    public TransferResponse transfer(@Valid @RequestBody CreateTransferRequest request){
        return transferOrchestrator.createAndExecute(request);
    }

}
