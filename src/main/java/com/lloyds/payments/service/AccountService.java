package com.lloyds.payments.service;

import com.lloyds.payments.dto.ReportSummaryDTO;
import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.exception.InvalidInputException;
import com.lloyds.payments.repository.AccountRepository;
import com.lloyds.payments.repository.PaymentOutcomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PaymentOutcomeRepository repository;
    private final AccountRepository accountRepository;


    public List<PaymentOutcome> getAccountHistory(String accountId) {

        accountRepository.findById(accountId).orElseThrow(() -> new InvalidInputException("Account not found: " + accountId));

        return repository.findByDebitAccountIdOrCreditAccountIdOrderByProcessedAtDesc(
                accountId, accountId);
}
}