package com.lloyds.payments.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.lloyds.payments.entity.PaymentOutcome;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void PaymentOutcomeProcessed(PaymentOutcome paymentOutcome){
        log.info(
                "Publishing payment event paymentId={}, debitAccountId={}",
                paymentOutcome.getPaymentId(),
                paymentOutcome.getDebitAccountId()
        );
        kafkaTemplate.send("payments.processed", paymentOutcome.getDebitAccountId(), paymentOutcome)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info(
                                "Message published successfully topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error(
                                "Kafka publish failed paymentId={}",
                                paymentOutcome.getPaymentId(),
                                ex
                        );
                    }
                });
    }
}


