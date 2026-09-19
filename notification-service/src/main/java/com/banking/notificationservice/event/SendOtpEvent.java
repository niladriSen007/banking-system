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
public class SendOtpEvent {
    private String referenceNumber;
    private String senderAccountNumber;
    private BigDecimal amount;
    private String reason;
    private String otp;
}
