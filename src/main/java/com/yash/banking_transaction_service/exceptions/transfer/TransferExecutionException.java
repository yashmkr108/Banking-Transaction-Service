package com.yash.banking_transaction_service.exceptions.transfer;

public class TransferExecutionException extends RuntimeException {
    public TransferExecutionException(String message) {
        super(message);
    }
}
