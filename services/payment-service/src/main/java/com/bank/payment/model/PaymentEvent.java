package com.bank.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEvent(
        String transactionId,
        String sourceAccount,
        String targetAccount,
        BigDecimal amount,
        Instant timestamp
) {}
