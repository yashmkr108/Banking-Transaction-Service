package com.yash.banking_transaction_service.exceptions.idempotency;

public class IdempotencyInProgressException extends RuntimeException {

    public IdempotencyInProgressException() {
        super("Request with this idempotency key is already being processed");
    }
}