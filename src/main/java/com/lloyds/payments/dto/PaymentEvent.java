package com.lloyds.payments.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentEvent {

    private String paymentId;

    private String debitAccountId;

    private String creditAccountId;

    private BigDecimal amount;

    private Currency currency;

    private String reference;

    private LocalDateTime createdTime;
}
