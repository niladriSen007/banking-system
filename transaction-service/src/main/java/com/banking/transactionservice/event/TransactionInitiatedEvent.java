package com.banking.transactionservice.event;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInitiatedEvent {
    private String referenceNumber;
    private String senderAccountNumber;
    private String recipientAccountNumber;
    private BigDecimal amount;
    private String description;
}
