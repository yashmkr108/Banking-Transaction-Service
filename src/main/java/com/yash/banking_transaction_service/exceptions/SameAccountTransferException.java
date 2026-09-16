package com.yash.banking_transaction_service.exceptions;

public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException() {
        super("Source and destination accounts must be different");
    }
}
