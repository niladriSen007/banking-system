package com.banking.transactionservice.repository;


import com.banking.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> getTransactionsByReferenceNumber(String transactionReferenceNumber);

    List<Transaction> findBySenderAccountNumberOrderByCreatedAtDesc(Long accountNumber);

    @Query(value = """
                SELECT COUNT(*) FROM Transaction t WHERE t.senderAccountNumber = :accountNumber
            """)
    Long getTransactionCount(@Param("accountNumber") Long accountNumber);

    Optional<Transaction> findByReferenceNumber(String referenceNumber);
}
