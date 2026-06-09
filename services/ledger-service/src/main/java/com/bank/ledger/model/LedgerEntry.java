package com.bank.ledger.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private String account;

    @Enumerated(EnumType.STRING)
    private EntryType type;

    private BigDecimal amount;

    private Instant timestamp;

    protected LedgerEntry() {
    }

    public LedgerEntry(
            String transactionId,
            String account,
            EntryType type,
            BigDecimal amount,
            Instant timestamp) {

        this.transactionId = transactionId;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // getters
}