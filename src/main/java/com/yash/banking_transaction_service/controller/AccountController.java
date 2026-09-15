package com.yash.banking_transaction_service.controller;

import com.yash.banking_transaction_service.dto.AccountResponse;
import com.yash.banking_transaction_service.dto.CreateAccountRequest;
import com.yash.banking_transaction_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getByAccountNumber(accountNumber);
    }

    @PatchMapping("/{accountNumber}/close")
    public AccountResponse closeAccount(@PathVariable String accountNumber){
        return accountService.closeAccount(accountNumber);
    }

    @PatchMapping("/{accountNumber}/activate")
    public AccountResponse activateAccount(@PathVariable String accountNumber){
        return accountService.activateAccount(accountNumber);
    }

    @PatchMapping("/{accountNumber}/block")
    public AccountResponse blockAccount(@PathVariable String accountNumber){
        return accountService.blockAccount(accountNumber);
    }

}
