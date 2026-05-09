package com.lloyds.payments.kafka;

import java.math.BigDecimal;
import java.time.Instant;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

	private static final BigDecimal HOLD_LIMIT = new BigDecimal("250000");

	private final AccountRepository accountRepository;
	private final PaymentOutcomeRepository paymentOutcomeRepository;
	private final PaymentProducer paymentProducer;

	@RetryableTopic(attempts = "4", dltTopicSuffix = ".dlq")
	@KafkaListener(topics = "payments.submitted", groupId = "payment-processor-group")
	public void consume(PaymentEvent paymentEvent, @Header(KafkaHeaders.RECEIVED_KEY) String key,
			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition, @Header(KafkaHeaders.OFFSET) long offset) {

		long startTime = System.currentTimeMillis();

		log.info("Received payment event paymentId={}, key={}, partition={}, offset={}", paymentEvent.getPaymentId(),
				key, partition, offset);

		try {

			PaymentOutcome paymentOutcome = buildPaymentOutcome(paymentEvent);

			Account debitAccount = validateDebitAccount(paymentEvent, paymentOutcome);

			Account creditAccount = validateCreditAccount(paymentEvent, paymentOutcome);

			validateAccountStatus(debitAccount, "Debit", paymentOutcome);

			validateAccountStatus(creditAccount, "Credit", paymentOutcome);

			setPaymentStatus(paymentOutcome);

			paymentOutcome.setProcessedAt(Instant.now());

			paymentOutcome.setProcessingTimeMs(System.currentTimeMillis() - startTime);

			processPayment(paymentOutcome);

			log.info("Payment processed successfully paymentId={}", paymentEvent.getPaymentId());

		} catch (Exception ex) {

			log.error("Payment processing failed paymentId={}", paymentEvent.getPaymentId(), ex);

			throw ex;
		}
	}

	private PaymentOutcome buildPaymentOutcome(PaymentEvent paymentEvent) {

		PaymentOutcome paymentOutcome = new PaymentOutcome();

		paymentOutcome.setPaymentId(paymentEvent.getPaymentId());

		paymentOutcome.setDebitAccountId(paymentEvent.getDebitAccountId());

		paymentOutcome.setCreditAccountId(paymentEvent.getCreditAccountId());

		paymentOutcome.setAmount(paymentEvent.getAmount());

		paymentOutcome.setCurrency(paymentEvent.getCurrency());

		return paymentOutcome;
	}

	private Account validateDebitAccount(PaymentEvent paymentEvent, PaymentOutcome paymentOutcome) {

		return accountRepository.findById(paymentEvent.getDebitAccountId())
				.orElseThrow(() -> rejectPayment(paymentOutcome, HttpStatus.NOT_FOUND,
						"Debit Account not found: " + paymentEvent.getDebitAccountId()));
	}

	private Account validateCreditAccount(PaymentEvent paymentEvent, PaymentOutcome paymentOutcome) {

		return accountRepository.findById(paymentEvent.getCreditAccountId())
				.orElseThrow(() -> rejectPayment(paymentOutcome, HttpStatus.NOT_FOUND,
						"Credit Account not found: " + paymentEvent.getCreditAccountId()));
	}

	private void validateAccountStatus(Account account, String accountType, PaymentOutcome paymentOutcome) {

		if ("SUSPENDED".equals(account.getStatus())) {

			throw rejectPayment(paymentOutcome, HttpStatus.UNPROCESSABLE_CONTENT,
					accountType + " Account " + account.getAccountId() + " is suspended");
		}
	}

	private void setPaymentStatus(PaymentOutcome paymentOutcome) {

		if (paymentOutcome.getAmount().compareTo(HOLD_LIMIT) > 0) {

			paymentOutcome.setStatus("HELD");

		} else {

			paymentOutcome.setStatus("SUCCESS");
		}
	}

	private ResponseStatusException rejectPayment(PaymentOutcome paymentOutcome, HttpStatus status, String message) {

		paymentOutcome.setStatus("REJECTED");

		paymentOutcome.setProcessedAt(Instant.now());

		processPayment(paymentOutcome);

		return new ResponseStatusException(status, message);
	}

	private void processPayment(PaymentOutcome paymentOutcome) {

		log.info("Processing payment debitAccountId={}, amount={}, status={}", paymentOutcome.getDebitAccountId(),
				paymentOutcome.getAmount(), paymentOutcome.getStatus());

		paymentOutcomeRepository.save(paymentOutcome);

		paymentProducer.PaymentOutcomeProcessed(paymentOutcome);

		log.info("PaymentOutcome saved successfully paymentId={}", paymentOutcome.getPaymentId());
	}
}