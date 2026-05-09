package com.lloyds.payments.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lloyds.payments.entity.Account;
import com.lloyds.payments.entity.PaymentOutcome;
import com.lloyds.payments.repository.AccountRepository;
import com.lloyds.payments.repository.PaymentOutcomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PaymentOutcomeRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        loadAccounts();
        loadPayments();
    }

    private void loadAccounts() throws Exception {
        if (accountRepository.count() > 0) return;

        List<Account> accounts =
                readJson("data/accounts.json",
                        new TypeReference<List<Account>>() {});

        accountRepository.saveAll(accounts);

        System.out.println("Accounts loaded: " + accounts.size());
    }

    private void loadPayments() throws Exception {
        if (paymentRepository.count() > 0) return;

        List<PaymentOutcome> payments =
                readJson("data/payment.json",
                        new TypeReference<List<PaymentOutcome>>() {});


        payments.forEach(p -> {
            p.setStatus(randomStatus());
            p.setProcessedAt(
                    Instant.now().minusSeconds(
                            ThreadLocalRandom.current().nextInt(100000))
            );
            p.setProcessingTimeMs(
                    ThreadLocalRandom.current().nextLong(50, 1500)
            );
        });

        paymentRepository.saveAll(payments);

        System.out.println("Payments loaded: " + payments.size());
    }

    private <T> T readJson(String filePath,
                           TypeReference<T> typeReference) throws Exception {

        try (InputStream is =
                     new ClassPathResource(filePath).getInputStream()) {
            return objectMapper.readValue(is, typeReference);
        }
    }

    private String randomStatus() {
        String[] statuses = {"PROCESSED", "HELD", "REJECTED"};
        return statuses[
                ThreadLocalRandom.current()
                        .nextInt(statuses.length)
                ];
    }
}