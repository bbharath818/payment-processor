package com.lloyds.payments.service;


import com.lloyds.payments.dto.MetricSummaryDTO;
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
    private  PaymentOutcomeRepository repo;

    private final AtomicLong totalProcessed = new AtomicLong();
    private final AtomicLong totalHeld = new AtomicLong();
    private final AtomicLong totalRejected = new AtomicLong();
    private final AtomicLong totalProcessingTime = new AtomicLong();

    public void updateMetrics(String status, long processingTime) {
        switch (status) {
            case "PROCESSED" -> totalProcessed.incrementAndGet();
            case "HELD" -> totalHeld.incrementAndGet();
            case "REJECTED" -> totalRejected.incrementAndGet();
        }
        totalProcessingTime.addAndGet(processingTime);
    }

    public MetricSummaryDTO getMetricsSummary() {
        long total = totalProcessed.get() + totalHeld.get() + totalRejected.get();

        double avg = total == 0 ? 0 :
                (double) totalProcessingTime.get() / total;

        return new MetricSummaryDTO(
                totalProcessed.get(),
                totalHeld.get(),
                totalRejected.get(),
                avg
        );
    }

    public ReportSummaryDTO getReportSummary() {
        Object[] result = repo.getSummary();

        return new ReportSummaryDTO(
                (Long) result[0],
                (BigDecimal) result[1]
        );
    }

    public Page<PaymentOutcome> getActivity(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (status != null)
            return repo.findByStatus(status, pageable);

        return repo.findAll(pageable);
    }

    public List<PaymentOutcome> getAccountHistory(String accountId) {
        return repo
                .findByDebitAccountIdOrCreditAccountIdOrderByCreatedAtDesc(
                        accountId, accountId);
    }
}