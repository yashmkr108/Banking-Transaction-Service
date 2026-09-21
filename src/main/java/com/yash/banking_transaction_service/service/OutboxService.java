package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.entity.OutboxEvent;
import com.yash.banking_transaction_service.enums.OutboxEventType;
import com.yash.banking_transaction_service.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {

    private final OutboxEventRepository repository;

    public OutboxService(OutboxEventRepository repository) {
        this.repository = repository;
    }

    public OutboxEvent create(
            OutboxEventType eventType,
            String aggregateType,
            String aggregateId,
            String payload
    ) {
        OutboxEvent event = new OutboxEvent(
                eventType,
                aggregateType,
                aggregateId,
                payload
        );

        return repository.save(event);
    }
}