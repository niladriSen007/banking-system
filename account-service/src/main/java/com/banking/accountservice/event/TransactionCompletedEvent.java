package com.banking.accountservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCompletedEvent {
	private String referenceNumber;
	private String senderAccountNumber;
	private BigDecimal amount;
	private String receiverAccountNumber;
	private String description;
}
