package com.lloyds.payments.service;


import com.lloyds.payments.dto.MetricsSummaryDTO;
import com.lloyds.payments.dto.ReportSummaryDTO;
import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.repository.PaymentOutcomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricService {

    @Autowired
    private  PaymentOutcomeRepository paymentOutcomeRepository;

    private final AtomicLong totalProcessed = new AtomicLong();
    private final AtomicLong totalHeld = new AtomicLong();
    private final AtomicLong totalRejected = new AtomicLong();
    private final AtomicLong totalProcessingTime = new AtomicLong();


    public MetricsSummaryDTO getMetricsSummary() {
        List<PaymentOutcome> payments = paymentOutcomeRepository.findAll();

        long processed = 0, held = 0, rejected = 0, totalTime = 0;

        for (PaymentOutcome p : payments) {
            switch (p.getStatus()) {
                case "PROCESSED" -> processed++;
                case "HELD" -> held++;
                case "REJECTED" -> rejected++;
            }
            totalTime += p.getProcessingTimeMs();
        }

        double avg = payments.isEmpty() ? 0 : (double) totalTime / payments.size();

        return new MetricsSummaryDTO(processed, held, rejected, avg);
    }

    public ReportSummaryDTO getReportSummary() {
        Object[] result = paymentOutcomeRepository.getSummary();

        return new ReportSummaryDTO(
                (Long) result[0],
                (BigDecimal) result[1]
        );
    }

    public Page<PaymentOutcome> getActivity(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (status != null)
            return paymentOutcomeRepository.findByStatus(status, pageable);

        return paymentOutcomeRepository.findAll(pageable);
    }

    public List<PaymentOutcome> getAccountHistory(String accountId) {
        return paymentOutcomeRepository
                .findByDebitAccountIdOrCreditAccountIdOrderByProcessedAtDesc(
                        accountId, accountId);
    }
}