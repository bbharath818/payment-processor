package com.lloyds.payments.controller;

import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.service.AccountService;
import com.lloyds.payments.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping("/{accountId}/history")
    public List<PaymentOutcome> history(
            @PathVariable String accountId) {
        final String decodedAccountId = URLDecoder.decode(accountId, StandardCharsets.UTF_8);
        return service.getAccountHistory(decodedAccountId);
    }
}