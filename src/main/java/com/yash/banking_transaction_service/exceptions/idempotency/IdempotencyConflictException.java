package com.yash.banking_transaction_service.exceptions.idempotency;

public class IdempotencyConflictException
        extends RuntimeException {

    public IdempotencyConflictException() {
        super(
                "Idempotency key was already used with a different request"
        );
    }
}