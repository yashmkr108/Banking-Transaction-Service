package com.yash.banking_transaction_service.generator;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
public class InstanceIdentity {
    private final String workerId = UUID.randomUUID().toString();
}
