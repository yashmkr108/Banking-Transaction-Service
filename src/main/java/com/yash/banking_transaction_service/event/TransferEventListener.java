package com.yash.banking_transaction_service.event;

import com.yash.banking_transaction_service.entity.AuditLog;
import com.yash.banking_transaction_service.enums.AuditAction;
import com.yash.banking_transaction_service.enums.AuditStatus;
import com.yash.banking_transaction_service.repository.AuditLogRepository;
import com.yash.banking_transaction_service.service.AuditService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransferEventListener {
    private final AuditService auditService;

    public TransferEventListener(
            AuditService auditService
    ) {
        this.auditService = auditService;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleTransferCompletedEvent(TransferCompletedEvent event) {
        auditService.createTransferCompleteAudit(event.reference());
    }

}
