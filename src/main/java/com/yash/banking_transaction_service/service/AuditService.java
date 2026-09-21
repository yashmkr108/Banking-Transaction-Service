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
}
