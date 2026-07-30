package com.banking.paymentservice.service;

import com.banking.paymentservice.dto.CreatePaymentRequest;
import com.banking.paymentservice.dto.PaymentOrderResponse;
import com.razorpay.RazorpayException;

import java.util.Map;

public interface IPaymentService {
    PaymentOrderResponse createPaymentOrder(
            CreatePaymentRequest request) throws RazorpayException;
    void handleWebhook(Map<String, Object> payload);
}
