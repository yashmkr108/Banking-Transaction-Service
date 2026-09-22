package com.yash.banking_transaction_service.orchestration;

import com.yash.banking_transaction_service.entity.OutboxEvent;
import com.yash.banking_transaction_service.service.EmailService;
import com.yash.banking_transaction_service.service.outbox.OutboxClaimService;
import com.yash.banking_transaction_service.service.outbox.OutboxCompletionService;
import org.springframework.stereotype.Service;

@Service
public class OutboxPublisher {

    private final OutboxClaimService outboxClaimService;
    private final OutboxCompletionService outboxCompletionService;
    private final EmailService emailService;

    public OutboxPublisher(
            OutboxClaimService outboxClaimService,
            OutboxCompletionService outboxCompletionService,
            EmailService emailService
    )
    {
        this.outboxClaimService = outboxClaimService;
        this.outboxCompletionService = outboxCompletionService;
        this.emailService = emailService;
    }

    public void publishNext(String workerId) {

        OutboxEvent event =
                outboxClaimService.claimNextEvent(workerId);

        if (event == null) {
            return;
        }

        emailService.sendTransferCompletedEmail(event);
        outboxCompletionService.markCompleted(event.getId());
    }
}