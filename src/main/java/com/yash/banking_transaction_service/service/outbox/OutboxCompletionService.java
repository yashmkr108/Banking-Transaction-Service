package com.yash.banking_transaction_service.service.outbox;

import com.yash.banking_transaction_service.entity.OutboxEvent;
import com.yash.banking_transaction_service.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxCompletionService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxCompletionService(
            OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void markCompleted(Long eventId) {

        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Outbox event not found: " + eventId
                        ));

        event.markCompleted();
    }
}
