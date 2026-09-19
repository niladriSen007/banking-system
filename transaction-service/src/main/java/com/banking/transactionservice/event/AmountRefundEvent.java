package com.banking.transactionservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmountRefundEvent {
    private String referenceNumber;
    private String senderAccountNumber;
    private BigDecimal amount;
    private String reason;
}
