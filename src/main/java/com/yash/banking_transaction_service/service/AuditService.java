package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.entity.AuditLog;
import com.yash.banking_transaction_service.enums.AuditAction;
import com.yash.banking_transaction_service.enums.AuditStatus;
import com.yash.banking_transaction_service.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(
            AuditAction action,
            String reference,
            AuditStatus status,
            String message
    ) {
        AuditLog auditLog = new AuditLog(
                action,
                reference,
                status,
                message
        );

        auditLogRepository.save(auditLog);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTransferCompleteAudit(String reference) {
        AuditLog auditLog = new AuditLog(
                AuditAction.TRANSFER_COMPLETED,
                reference,
                AuditStatus.SUCCESS,
                "Created from AFTER_COMMIT event"
        );

        auditLogRepository.save(auditLog);
        throw new RuntimeException("Testing post-commit transaction");
    }

}
