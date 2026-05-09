package com.lloyds.payments.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDTO {

    private long totalProcessed;

    private long totalHeld;

    private long totalRejected;

    private BigDecimal totalProcessedAmount;

    private Instant startDate;

    private Instant endDate;
}