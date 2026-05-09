package com.lloyds.payments.kafka;

import com.lloyds.payments.dto.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void PaymentOutcomeProcessed(PaymentEvent paymentEvent){
        log.info(
                "Publishing payment event paymentId={}, debitAccountId={}",
                paymentEvent.getPaymentId(),
                paymentEvent.getDebitAccountId()
        );
        kafkaTemplate.send("payments.processed", paymentEvent.getDebitAccountId(), paymentEvent)
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
                                paymentEvent.getPaymentId(),
                                ex
                        );
                    }
                });
    }
}


