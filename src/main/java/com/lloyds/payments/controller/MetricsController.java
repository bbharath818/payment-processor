package com.lloyds.payments.controller;

import com.lloyds.payments.dto.MetricsSummaryDTO;
import com.lloyds.payments.dto.ReportSummaryDTO;
import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MetricsController {

    private final MetricService metricService;

    @GetMapping("/metrics/summary")
    public ResponseEntity<MetricsSummaryDTO> getMetricsSummary() {
        MetricsSummaryDTO metricsSummary = metricService.getMetricsSummary();
        return ResponseEntity.ok(metricsSummary);
    }



    @GetMapping("/reports/activity")
    public ResponseEntity<Page<PaymentOutcome>> getActivity(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PaymentOutcome> activity = metricService.getActivity(status, page, size);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/accounts/{accountId}/history")
    public ResponseEntity<List<PaymentOutcome>> getAccountHistory(
            @PathVariable String accountId) {

        List<PaymentOutcome> accountHistory = metricService.getAccountHistory(accountId);
        return ResponseEntity.ok(accountHistory);
    }
}