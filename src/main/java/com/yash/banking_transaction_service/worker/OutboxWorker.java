package com.yash.banking_transaction_service.worker;

import com.yash.banking_transaction_service.generator.InstanceIdentity;
import com.yash.banking_transaction_service.orchestration.OutboxPublisher;
import com.yash.banking_transaction_service.service.outbox.OutboxRecoveryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OutboxWorker {

    private static final int BATCH_SIZE = 10;

    private final OutboxPublisher outboxPublisher;
    private final OutboxRecoveryService outboxRecoveryService;
    private final InstanceIdentity instanceIdentity;

    public OutboxWorker(
            OutboxPublisher outboxPublisher,
            OutboxRecoveryService outboxRecoveryService,
            InstanceIdentity instanceIdentity
    ) {
        this.outboxPublisher = outboxPublisher;
        this.outboxRecoveryService = outboxRecoveryService;
        this.instanceIdentity = instanceIdentity;
    }

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {

        Instant cutoff = Instant.now().minusSeconds(45);

        outboxRecoveryService.recoverStaleEvents(cutoff);

        String workerId = "worker-" + instanceIdentity.getWorkerId();

        for (int i = 0; i < BATCH_SIZE; i++) {

            boolean processed = outboxPublisher.publishNext(workerId);

            if (!processed) break;
        }
    }
}
