package com.banking.transactionservice.repository;


import com.banking.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Optional<Transaction> getTransactionsByReferenceNumber(String transactionReferenceNumber);

    List<Transaction> findBySenderAccountNumberOrderByCreatedAtDesc(Long accountNumber);
}
