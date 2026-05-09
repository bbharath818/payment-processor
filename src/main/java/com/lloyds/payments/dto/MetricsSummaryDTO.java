package com.lloyds.payments.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class MetricsSummaryDTO {
    private long totalProcessed;
    private long totalHeld;
    private long totalRejected;
    private double avgProcessingTimeMs;
}