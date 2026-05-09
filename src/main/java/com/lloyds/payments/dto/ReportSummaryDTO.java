package com.lloyds.payments.dto;

import lombok.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ReportSummaryDTO {
    private long totalRecords;
    private BigDecimal totalAmount;
}