package com.lloyds.payments.service;

import com.lloyds.payments.dto.ReportSummaryDTO;
import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.repository.PaymentOutcomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PaymentOutcomeRepository repository;

    public ReportSummaryDTO getReportSummary() {

        List<PaymentOutcome> payments = repository.findAll();

        long processed = 0;
        long held = 0;
        long rejected = 0;

        BigDecimal totalProcessedAmount = BigDecimal.ZERO;

        Instant startDate = null;
        Instant endDate = null;

        for (PaymentOutcome p : payments) {

            // count by status
            switch (p.getStatus()) {
                case "PROCESSED" -> {
                    processed++;
                    totalProcessedAmount =
                            totalProcessedAmount.add(p.getAmount());
                }
                case "HELD" -> held++;
                case "REJECTED" -> rejected++;
            }

            // min date
            if (startDate == null ||
                    p.getProcessedAt().isBefore(startDate)) {
                startDate = p.getProcessedAt();
            }

            // max date
            if (endDate == null ||
                    p.getProcessedAt().isAfter(endDate)) {
                endDate = p.getProcessedAt();
            }
        }

        return new ReportSummaryDTO(
                processed,
                held,
                rejected,
                totalProcessedAmount,
                startDate,
                endDate
        );
    }


}