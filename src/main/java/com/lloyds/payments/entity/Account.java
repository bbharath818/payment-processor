package com.lloyds.payments.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    private String accountId;

    private String accountName;

    private String accountType;   // PERSONAL, BUSINESS, SAVINGS

    private String status;        // ACTIVE, SUSPENDED

    private String currency;      // GBP, EUR, USD

    private LocalDate openedDate;
}