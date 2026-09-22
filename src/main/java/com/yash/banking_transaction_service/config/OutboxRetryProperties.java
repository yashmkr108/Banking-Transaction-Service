package com.yash.banking_transaction_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "outbox.retry")
public record OutboxRetryProperties(
        int maxAttempts
) {
}
