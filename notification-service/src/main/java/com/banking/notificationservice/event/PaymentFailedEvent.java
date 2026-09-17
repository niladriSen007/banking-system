package com.banking.notificationservice.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentFailedEvent {
	Long paymentId;
	Long accountNumber;
	BigDecimal amount;
	String reason;
}
