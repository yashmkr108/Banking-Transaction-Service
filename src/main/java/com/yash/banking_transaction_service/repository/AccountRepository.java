package com.yash.banking_transaction_service.repository;

import com.yash.banking_transaction_service.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Account a WHERE a.accountNumber = :accountNumber
            """)
    Optional<Account> findByAccountNumberForUpdate(
            @Param("accountNumber") String accountNumber
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Account a WHERE a.id = :id
            """)
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

}
