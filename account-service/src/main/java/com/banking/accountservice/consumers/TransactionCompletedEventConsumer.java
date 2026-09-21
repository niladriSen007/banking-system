package com.banking.accountservice.consumers;

import com.banking.accountservice.event.TransactionCompletedEvent;
import com.banking.accountservice.service.IAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionCompletedEventConsumer {

    private final IAccountService accountService;

    /**
     * Consume the Transaction completion event
     * Credits the Recipients account
     *
     * @param payload
     */
    @KafkaListener(topics = "transaction.completed",groupId = "account-service-test-group")
    public void handleTransactionCompletedEvent(@Payload TransactionCompletedEvent payload) {
        String recipientAccountNumber =
                String.valueOf(payload.getReceiverAccountNumber());

        BigDecimal amount =
                new BigDecimal(String.valueOf(payload.getAmount()));

        log.info(
                "Received transaction completed event for recipient account number: {}",
                recipientAccountNumber
        );
        accountService.creditBalnce(recipientAccountNumber, amount);
    }


}
