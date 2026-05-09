package com.lloyds.payments.kafka;

import com.lloyds.payments.dto.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentConsumer {
    @RetryableTopic(
            attempts = "4",
            dltTopicSuffix = ".dlq"
    )

    @KafkaListener(topics = "payments.submitted", groupId = "payment-processor-group" )
    public void consume( PaymentEvent paymentEvent,
                         @Header(KafkaHeaders.RECEIVED_KEY)
                         String key,
                         @Header(KafkaHeaders.RECEIVED_PARTITION)
                         int partition,
                         @Header(KafkaHeaders.OFFSET)
                         long offset)  {
        log.info("PaymentConsumer=====Received payment event: {}", paymentEvent);
        try {

            log.info(
                    "PaymentConsumer===Received payment event paymentId={}, key={}, partition={}, offset={}",
                    paymentEvent.getPaymentId(), key, partition, offset );
            // BUSINESS LOGIC
            processPayment(paymentEvent);
            log.info("PaymentConsumer====Payment processed successfully paymentId={}", paymentEvent.getPaymentId());
        } catch (Exception ex) {
            log.error("PaymentConsumer===Payment processing failed paymentId={}",
                    paymentEvent.getPaymentId(), ex );  // no acknowledge -> retry
            throw ex;
        }
        log.info("PaymentConsumer====Payment processed successfully");
    }

    private void processPayment(PaymentEvent paymentEvent) {

        log.info(
                "PaymentConsumer====Processing debitAccountId={}, amount={}",
                paymentEvent.getDebitAccountId(),
                paymentEvent.getAmount()
        );

        // DB update
        // Fraud validation
        // Payment settlement
        // Audit persistence
    }
}