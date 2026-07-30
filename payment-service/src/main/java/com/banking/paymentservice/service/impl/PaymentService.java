package com.banking.paymentservice.service.impl;

import com.banking.paymentservice.dto.CreatePaymentRequest;
import com.banking.paymentservice.dto.PaymentOrderResponse;
import com.banking.paymentservice.entity.PaymentStatus;
import com.banking.paymentservice.entity.Payments;
import com.banking.paymentservice.event.PaymentCompletedEvent;
import com.banking.paymentservice.event.PaymentFailedEvent;
import com.banking.paymentservice.producer.PaymentCompletedProducer;
import com.banking.paymentservice.producer.PaymentFailedProducer;
import com.banking.paymentservice.repository.PaymentRepository;
import com.banking.paymentservice.service.IPaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCompletedProducer paymentCompletedProducer;
    private final PaymentFailedProducer paymentFailedProducer;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    private static final String PAYMENT_COMPLETED_TOPIC = "payment.completed";
    private static final String PAYMENT_FAILED_TOPIC = "payment.failed";


    @Override
    public PaymentOrderResponse createPaymentOrder(CreatePaymentRequest request) throws RazorpayException {
        log.info("Creating payment order for account: {} amount: {}",
                request.getAccountNumber(), request.getAmount());

        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        // Amount in paise (1 INR = 100 paise)
        int amountInPaise = request.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .intValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "rcpt_" + UUID.randomUUID().toString().replace("-", "").substring(0, 30));

        Order razorpayOrder = razorpay.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id").toString();
        log.info("Razorpay order created: {}", razorpayOrderId);

        Payments payment = Payments.builder()
                .razorpayOrderId(razorpayOrderId)
                .accountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .currency("INR")
                .status(PaymentStatus.CREATED)
                .description(request.getDescription())
                .build();

        Payments savedPayment = paymentRepository.save(payment);
        return new PaymentOrderResponse(
                savedPayment.getId().toString(),
                razorpayOrderId,
                request.getAmount(),
                payment.getCurrency(),
                keyId,
                PaymentStatus.CREATED.toString()
        );
    }

    @Override
    public void handleWebhook(Map<String, Object> payload) {
        log.info("Received Razorpay webhook: {}", payload.get("event"));

        String event = (String) payload.get("event");

        if ("payment.captured".equals(event)) {
            handlePaymentSuccess(payload);
        } else if ("payment.failed".equals(event)) {
            handlePaymentFailure(payload);
        }
    }


    private void handlePaymentSuccess(Map<String, Object> payload) {
        try {
            Map<String, Object> paymentData = extractPaymentData(payload);
            String orderId = (String) paymentData.get("order_id");
            String paymentId = (String) paymentData.get("id");

            Payments payment = paymentRepository.findByRazorpayOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException(
                            "Payment not found for order: " + orderId));

            payment.setRazorpayPaymentId(paymentId);
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

            // Publish payment completed event
            PaymentCompletedEvent paymentCompletedEvent = PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .accountNumber(payment.getAccountNumber())
                    .amount(payment.getAmount())
                    .razorpayPaymentId(paymentId)
                    .build();

            paymentCompletedProducer.publishPaymentCompletedEvent(
                    PAYMENT_COMPLETED_TOPIC,
                    payment.getId().toString(),
                    paymentCompletedEvent
            );

            log.info("Payment completed: {}", payment.getId());

        } catch (Exception e) {
            log.error("Error handling payment success: {}", e.getMessage());
        }
    }

    private void handlePaymentFailure(Map<String, Object> payload) {
        try {
            Map<String, Object> paymentData = extractPaymentData(payload);
            String orderId = (String) paymentData.get("order_id");

            Payments payment = paymentRepository.findByRazorpayOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException(
                            "Payment not found for order: " + orderId));

            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment failed via Razorpay");
            paymentRepository.save(payment);

            // Publish payment.failed event ← ADD THIS
            PaymentFailedEvent paymentFailedEvent = PaymentFailedEvent.builder()
                    .paymentId(payment.getId())
                    .accountNumber(payment.getAccountNumber())
                    .amount(payment.getAmount())
                    .reason("Payment failed via Razorpay")
                    .build();
            paymentFailedProducer.publishPaymentFailedEvent(
                    PAYMENT_FAILED_TOPIC,
                    payment.getId().toString(),
                    paymentFailedEvent
            );

            log.warn("Payment failed: {}", payment.getId());

        } catch (Exception e) {
            log.error("Error handling payment failure: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractPaymentData(Map<String, Object> payload) {
        Map<String, Object> entity = (Map<String, Object>) payload.get("payload");
        Map<String, Object> paymentWrapper = (Map<String, Object>) entity.get("payment");
        return (Map<String, Object>) paymentWrapper.get("entity");
    }
}
