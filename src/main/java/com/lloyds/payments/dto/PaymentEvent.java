package com.lloyds.payments.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentEvent {

    private UUID paymentId;

    private String debitAccountId;

    private String creditAccountId;

    private BigDecimal amount;

    private String currency;

    private String reference;

    private LocalDateTime createdTime;
}
