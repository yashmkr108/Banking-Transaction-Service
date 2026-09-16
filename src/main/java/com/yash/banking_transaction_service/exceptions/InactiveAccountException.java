package com.yash.banking_transaction_service.exceptions;

public class InactiveAccountException extends RuntimeException{
    public InactiveAccountException(){
        super("Both source and destination accounts must be active");
    }
}
