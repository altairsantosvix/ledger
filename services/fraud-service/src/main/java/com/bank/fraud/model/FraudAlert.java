package com.bank.fraud.model;

import java.math.BigDecimal;

public record FraudAlert(
        String transactionId,
        String sourceAccount,
        String targetAccount,
        BigDecimal amount,
        String reason
) {}