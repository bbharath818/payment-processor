package com.lloyds.payments.controller;

import com.lloyds.payments.dto.MetricsSummaryDTO;
import com.lloyds.payments.dto.ReportSummaryDTO;
import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.service.MetricService;
import com.lloyds.payments.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService service;

    @GetMapping("/summary")
    public ReportSummaryDTO summary() {
        return service.getReportSummary();
    }

//    @GetMapping("/activity")
//    public Page<PaymentOutcome> activity(
//            @RequestParam(required = false) PaymentStatus status,
//            @RequestParam(required = false) String accountId,
//            Pageable pageable) {
//
//        return service.getActivity(status, accountId, pageable);
//    }
}