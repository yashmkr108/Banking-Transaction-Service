package com.yash.banking_transaction_service.orchestration;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.entity.IdempotencyRecord;
import com.yash.banking_transaction_service.entity.Transfer;
import com.yash.banking_transaction_service.enums.IdempotencyStatus;
import com.yash.banking_transaction_service.enums.TransferStatus;
import com.yash.banking_transaction_service.exceptions.idempotency.IdempotencyConflictException;
import com.yash.banking_transaction_service.exceptions.idempotency.IdempotencyInProgressException;
import com.yash.banking_transaction_service.exceptions.transfer.TransferExecutionException;
import com.yash.banking_transaction_service.generator.RequestFingerprintGenerator;
import com.yash.banking_transaction_service.mapper.TransferMapper;
import com.yash.banking_transaction_service.service.IdempotencyService;
import com.yash.banking_transaction_service.service.TransferService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class TransferOrchestrator {

    private final TransferService transferService;
    private final IdempotencyService idempotencyService;
    private final RequestFingerprintGenerator fingerprintGenerator;
    private final TransferMapper transferMapper;

    public TransferOrchestrator(
            TransferService transferService,
            IdempotencyService idempotencyService,
            RequestFingerprintGenerator fingerprintGenerator,
            TransferMapper transferMapper
    ) {
        this.transferService = transferService;
        this.idempotencyService = idempotencyService;
        this.fingerprintGenerator = fingerprintGenerator;
        this.transferMapper = transferMapper;
    }

    public TransferResponse createAndExecute(String idempotencyKey, CreateTransferRequest request) {

        String fingerprint =
                fingerprintGenerator.generate(request);

        TransferResponse transfer;

        try {

            transfer = transferService.createTransfer(request, idempotencyKey);

        } catch (DataIntegrityViolationException e) {
            // Request arrive with same idempotency key

            IdempotencyRecord existing =
                    idempotencyService.findRecord(idempotencyKey)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Idempotency record not found"
                                    )
                            );

            // Same key different fingerprint -> key can not be same for two request
            if (!existing.getRequestFingerprint().equals(fingerprint)) {
                throw new IdempotencyConflictException();
            }

            // same key same fingerprint in process -> duplicate request
            if (existing.getStatus() == IdempotencyStatus.PROCESSING) {

                Transfer existingTransfer = transferService.getTransfer(existing.getTransferReference());

                if (existingTransfer.getStatus() == TransferStatus.COMPLETED) {

                    idempotencyService.markCompleted(idempotencyKey);

                    return transferMapper.toResponse(existingTransfer);
                }

                if (existingTransfer.getStatus() == TransferStatus.FAILED) {

                    idempotencyService.markFailed(idempotencyKey);

                    return transferMapper.toResponse(existingTransfer);
                }

                throw new IdempotencyInProgressException();
            }

            if (existing.getStatus() == IdempotencyStatus.COMPLETED) {

                String reference = existing.getTransferReference();

                return transferService.getTransferByReference(reference);
            }

            if(existing.getStatus() == IdempotencyStatus.FAILED){

                String reference = existing.getTransferReference();

                return transferService.getTransferByReference(reference);
            }
            throw new IllegalStateException(
                    "Unhandled idempotency status: "
                            + existing.getStatus()
            );
        }

        try {

            TransferResponse response = transferService.executeTransfer(transfer.reference());

            idempotencyService.markCompleted(idempotencyKey);

            return response;

        } catch (TransferExecutionException e) {

            TransferResponse response = transferService.failTransfer(transfer.reference());

            idempotencyService.markFailed(idempotencyKey);

            return response;
        }

    }
}
