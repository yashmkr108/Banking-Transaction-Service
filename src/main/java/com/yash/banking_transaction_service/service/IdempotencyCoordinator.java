package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.exceptions.transfer.generator.RequestFingerprintGenerator;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyCoordinator {

    private final TransferService transferService;
    private final IdempotencyService idempotencyService;
    private final RequestFingerprintGenerator fingerprintGenerator;

    public IdempotencyCoordinator(
            TransferService transferService,
            IdempotencyService idempotencyService,
            RequestFingerprintGenerator fingerprintGenerator
    ) {
        this.transferService = transferService;
        this.idempotencyService = idempotencyService;
        this.fingerprintGenerator = fingerprintGenerator;
    }



}
