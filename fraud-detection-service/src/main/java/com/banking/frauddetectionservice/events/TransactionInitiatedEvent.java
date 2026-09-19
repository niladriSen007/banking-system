package com.banking.frauddetectionservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
