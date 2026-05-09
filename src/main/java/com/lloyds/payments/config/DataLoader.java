package com.lloyds.payments.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lloyds.payments.entity.Account;
import com.lloyds.payments.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {

        // avoid duplicate insert
        if (accountRepository.count() > 0) {
            return;
        }

        InputStream inputStream =
                new ClassPathResource("accounts.json").getInputStream();

        List<Account> accounts =
                objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<Account>>() {}
                );

        accountRepository.saveAll(accounts);

        System.out.println("Accounts loaded successfully: "
                + accounts.size());
    }
}