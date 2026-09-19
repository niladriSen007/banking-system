package com.banking.transactionservice.repository;


import com.banking.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> getTransactionsByReferenceNumber(String transactionReferenceNumber);

    List<Transaction> findBySenderAccountNumberOrderByInitiatedAtDesc(String senderAccountNumber);

    @Query(value = """
                SELECT COUNT(*) FROM Transaction t WHERE t.senderAccountNumber = :accountNumber
            """)
    Long getTransactionCount(@Param("accountNumber") String accountNumber);

    Optional<Transaction> findByReferenceNumber(String referenceNumber);
}
