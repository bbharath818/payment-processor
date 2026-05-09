package com.lloyds.payments.kafka;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.lloyds.payments.dto.PaymentEvent;
import com.lloyds.payments.entity.Account;
import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.repository.AccountRepository;
import com.lloyds.payments.repository.PaymentOutcomeRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentConsumer {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PaymentOutcomeRepository paymentOutcomeRepository;
	
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
            
            Optional<Account> debitAccount = accountRepository.findById(paymentEvent.getDebitAccountId());
            
            if(debitAccount.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"User with Debit Account Id " + paymentEvent.getDebitAccountId() + " not found");
            }
            
            Optional<Account> creditAccount = accountRepository.findById(paymentEvent.getDebitAccountId());
            if(creditAccount.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"User with Credit Account Id " + paymentEvent.getCreditAccountId() + " not found");
            }
            
            if(debitAccount.get().getStatus().equals("SUSPENDED")) {
				throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
						"Debit Account " + paymentEvent.getCreditAccountId() + " is suspended");
            }
            
            if(creditAccount.get().getStatus().equals("SUSPENDED")) {
				throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
						"Credit Account " + paymentEvent.getCreditAccountId() + " is suspended");
            }
            
            if(debitAccount.get().getStatus().equals("ACTIVE") && creditAccount.get().getStatus().equals("ACTIVE")) {
            	// Publish to Kafka
                processPayment(paymentEvent);
            }
            
            log.info("PaymentConsumer====Payment processed successfully paymentId={}", paymentEvent.getPaymentId());
        } catch (Exception ex) {
            log.error("PaymentConsumer===Payment processing failed paymentId={}",
                    paymentEvent.getPaymentId(), ex );  // no acknowledge -> retry
            throw ex;
        }
        log.info("PaymentConsumer====Payment processed successfully");
    }
    
    private void processPayment(PaymentEvent paymentEvent) {

        long startTime = System.currentTimeMillis();

        log.info(
                "PaymentConsumer====Processing debitAccountId={}, amount={}",
                paymentEvent.getDebitAccountId(),
                paymentEvent.getAmount()
        );

        // Convert PaymentEvent -> PaymentOutcome
        PaymentOutcome paymentOutcome = new PaymentOutcome();

        paymentOutcome.setPaymentId(paymentEvent.getPaymentId());
        paymentOutcome.setDebitAccountId(paymentEvent.getDebitAccountId());
        paymentOutcome.setCreditAccountId(paymentEvent.getCreditAccountId());
        paymentOutcome.setAmount(paymentEvent.getAmount());
        paymentOutcome.setCurrency(paymentEvent.getCurrency());
        paymentOutcome.setStatus("SUCCESS");
        paymentOutcome.setProcessedAt(Instant.now());

        long processingTime = System.currentTimeMillis() - startTime;
        paymentOutcome.setProcessingTimeMs(processingTime);

        // Save into DB
        paymentOutcomeRepository.save(paymentOutcome);

        log.info(
                "PaymentConsumer====PaymentOutcome saved successfully paymentId={}",
                paymentEvent.getPaymentId()
        );
    }
}