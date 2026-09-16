package com.yash.banking_transaction_service.exceptions;

public class InvalidTransferStatusException extends RuntimeException {
    public InvalidTransferStatusException(String message) {
        super(message);
    }
}
