package com.lloyds.payments.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class MetricSummaryDTO {
    private long totalProcessed;
    private long totalHeld;
    private long totalRejected;
    private double avgProcessingTimeMs;
}