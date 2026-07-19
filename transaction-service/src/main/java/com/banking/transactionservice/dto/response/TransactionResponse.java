package com.banking.transactionservice.dto.response;

import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long senderAccountNumber;
    private Long receiverAccountNumber;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private String referenceNumber;
    private BigDecimal amount;
    private String failureReason;
    private String description;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
}
