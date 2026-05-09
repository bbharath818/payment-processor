package com.lloyds.payments.kafka;

import com.lloyds.payments.dto.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    @KafkaListener(topics = "submitted", groupId = "payment-group")
    public void consumePaymentEvent(PaymentEvent paymentEvent) {
        // Process the payment event
        System.out.println("Consumed payment event: " + paymentEvent);

        // Add your business logic here
    }
}