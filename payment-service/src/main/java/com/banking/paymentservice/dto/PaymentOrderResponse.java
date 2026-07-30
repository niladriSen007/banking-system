package com.banking.paymentservice.dto;

import java.math.BigDecimal;

public record PaymentOrderResponse(
        String paymentId,
        String razorpayOrderId,
        BigDecimal amount,
        String currency,
        String razorpayKeyId,
        String status
) {
}
