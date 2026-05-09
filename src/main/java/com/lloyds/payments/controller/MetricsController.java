package com.lloyds.payments.controller;

import com.lloyds.payments.service.MetricService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    private MetricService metricService;

    @GetMapping
    public List<String> getAllMetrics() {
        // Fetch all available metrics from the MeterRegistry
        metricService                .map(Meter::getId)
                .map(id -> id.getName() + " (" + id.getType() + ")")
                .collect(Collectors.toList());
    }
}