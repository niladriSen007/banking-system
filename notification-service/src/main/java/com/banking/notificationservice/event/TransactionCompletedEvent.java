package com.banking.notificationservice.event;

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
	private Long senderAccountNumber;
	private BigDecimal amount;
	private Long receiverAccountNumber;
	private String description;
}