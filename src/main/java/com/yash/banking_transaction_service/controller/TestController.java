package com.yash.banking_transaction_service.controller;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.exceptions.transfer.generator.RequestFingerprintGenerator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final RequestFingerprintGenerator hashGenerator;

    public TestController(RequestFingerprintGenerator hashGenerator){
        this.hashGenerator = hashGenerator;
    }

    @GetMapping("/hash")
    public String generateHash(@Valid @RequestBody CreateTransferRequest request){
        return hashGenerator.generate(request);
    }
}
