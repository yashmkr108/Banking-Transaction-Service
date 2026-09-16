package com.yash.banking_transaction_service.exceptions;

public class TransferExecutionException extends RuntimeException {
    public TransferExecutionException(String message) {
        super(message);
    }
}
