package com.lloyds.payments.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentEvent {

	@NotNull(message = "Payment ID must not be null")
	private String paymentId;

	@NotBlank(message = "Debit account ID must not be blank")
	private String debitAccountId;

	@NotBlank(message = "Credit account ID must not be blank")
	private String creditAccountId;

	@NotNull(message = "Amount must not be null")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	private BigDecimal amount;

	@NotNull(message = "Currency must not be null")
	private String currency;

	@Size(max = 35, message = "Reference must not exceed 35 characters")
	private String reference;

	@NotNull(message = "Timestamp must not be null")
	@PastOrPresent(message = "Timestamp must not be in the future")
	private Instant timestamp;
}