package com.yash.banking_transaction_service.service.outbox;

import com.yash.banking_transaction_service.config.OutboxRetryProperties;
import com.yash.banking_transaction_service.enums.OutboxStatus;
import com.yash.banking_transaction_service.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OutboxRecoveryService {
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxRetryProperties retryProperties;

    public OutboxRecoveryService(
            OutboxEventRepository outboxEventRepository,
            OutboxRetryProperties retryProperties
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.retryProperties = retryProperties;
    }

    @Transactional
    public int recoverStaleEvents(Instant cutoff) {
        return outboxEventRepository.recoverStaleEvents(
                OutboxStatus.PROCESSING,
                OutboxStatus.PENDING,
                OutboxStatus.FAILED,
                retryProperties.maxAttempts(),
                cutoff
        );
    }

}
