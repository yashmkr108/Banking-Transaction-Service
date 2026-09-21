package com.yash.banking_transaction_service.worker;

import com.yash.banking_transaction_service.orchestration.OutboxPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxWorker {

    private final OutboxPublisher outboxPublisher;

    public OutboxWorker(OutboxPublisher outboxPublisher){
        this.outboxPublisher = outboxPublisher;
    }

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        outboxPublisher.publishNext();
    }
}
