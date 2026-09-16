package com.yash.banking_transaction_service.repository;

import com.yash.banking_transaction_service.entity.Transfer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByReference(String reference);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT t
                FROM Transfer t
                WHERE t.reference = :reference
            """)
    Optional<Transfer> findByReferenceForUpdate(
            @Param("reference") String reference
    );
}
