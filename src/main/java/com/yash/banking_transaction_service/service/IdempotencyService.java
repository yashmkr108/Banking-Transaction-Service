package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.entity.IdempotencyRecord;
import com.yash.banking_transaction_service.repository.IdempotencyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;

    public IdempotencyService(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    public IdempotencyRecord createRecord(
            String idempotencyKey,
            String requestFingerprint
    ) {
        IdempotencyRecord record =
                new IdempotencyRecord(idempotencyKey, requestFingerprint);

        return repository.save(record);
    }


    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> findRecord(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey);
    }


    @Transactional
    public void markCompleted(String idempotencyKey) {

        IdempotencyRecord record =
                repository.findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Idempotency record not found"
                                )
                        );

        record.complete();
    }

    @Transactional
    public void markFailed(String idempotencyKey) {

        IdempotencyRecord record =
                repository.findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Idempotency record not found"
                                )
                        );

        record.fail();
    }
}