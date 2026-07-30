package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.request.TransactionRequest;
import com.banking.transactionservice.dto.response.TransactionResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ITransactionService {
    TransactionResponse transferAmount(@Valid TransactionRequest transactionRequest);

    TransactionResponse getTransactionInfo(@Valid String transactionId);

    List<TransactionResponse> getTransactionHistory(Long accountNumber);

    TransactionResponse verifyOtp(String transactionReferenceNumber, @Valid String otp);

    Long getTotalTransactionCount(@Valid Long accountNumber);

    void processCleanTransaction(String referenceNumber);
}
