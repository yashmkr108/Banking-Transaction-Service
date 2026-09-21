package com.yash.banking_transaction_service.exceptions.email;

public class EmailDeliveryException extends RuntimeException {
    public EmailDeliveryException(String message,Throwable cause) {
        super(message,cause);
    }
}
