package com.yash.banking_transaction_service.controller;

import com.yash.banking_transaction_service.generator.AccountNumberGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountNumberTestController {

    private final AccountNumberGenerator accountNumberGenerator;

    public AccountNumberTestController(AccountNumberGenerator accountNumberGenerator) {
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @GetMapping("/test/account-number")
    public String generateAccountNumber() {
        return accountNumberGenerator.generate();
    }
}