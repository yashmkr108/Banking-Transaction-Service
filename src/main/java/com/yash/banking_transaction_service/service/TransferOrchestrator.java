package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.exceptions.TransferExecutionException;
import org.springframework.stereotype.Service;

@Service
public class TransferOrchestrator {

    private final TransferService transferService;

    public TransferOrchestrator(TransferService transferService) {
        this.transferService = transferService;
    }

    public TransferResponse createAndExecute(CreateTransferRequest request) {
        TransferResponse transfer = transferService.createTransfer(request);
        try {
            return transferService.executeTransfer(transfer.reference());
        } catch (TransferExecutionException e) {
            return transferService.failTransfer(transfer.reference());
        }
    }
}
