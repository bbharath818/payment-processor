package com.lloyds.payments.service;

import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.repository.PaymentOutcomeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentProcessorService {

    private final PaymentOutcomeRepository paymentOutcomeRepository;

    public PaymentProcessorService(PaymentOutcomeRepository paymentOutcomeRepository) {
        this.paymentOutcomeRepository = paymentOutcomeRepository;
    }

    public PaymentOutcome processPayment(UUID paymentId, String debitAccountId, String creditAccountId,
                                         String currency, String status, long processingTimeMs) {
        PaymentOutcome paymentOutcome = new PaymentOutcome();
        paymentOutcome.setPaymentId(paymentId);
        paymentOutcome.setDebitAccountId(debitAccountId);
        paymentOutcome.setCreditAccountId(creditAccountId);
        paymentOutcome.setCurrency(currency);
        paymentOutcome.setStatus(status);
        paymentOutcome.setProcessedAt(Instant.now());
        paymentOutcome.setProcessingTimeMs(processingTimeMs);

        return paymentOutcomeRepository.save(paymentOutcome);
    }
}