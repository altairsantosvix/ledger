package com.bank.payment.model;

import java.math.BigDecimal;

public record PaymentRequest(
        String sourceAccount,
        String targetAccount,
        BigDecimal amount,
        String currency,
        String idempotencyKey
) {}