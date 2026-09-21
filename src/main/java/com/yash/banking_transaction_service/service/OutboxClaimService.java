package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.entity.OutboxEvent;
import com.yash.banking_transaction_service.enums.OutboxStatus;
import com.yash.banking_transaction_service.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxClaimService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxClaimService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public OutboxEvent claimNextEvent(String workerId) {

        OutboxEvent event = outboxEventRepository
                .findFirstByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
                .orElse(null);

        if (event == null) {
            return null;
        }

        event.markProcessing(workerId);

        return event;
    }

}
