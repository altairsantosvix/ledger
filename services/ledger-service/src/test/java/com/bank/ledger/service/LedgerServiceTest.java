package com.bank.ledger.service;

import com.bank.ledger.model.EntryType;
import com.bank.ledger.model.LedgerEntry;
import com.bank.ledger.repository.LedgerRepository;
import com.bank.payment.model.PaymentEvent;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private LedgerRepository repository;

    private LedgerService ledgerService;

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService(repository, meterRegistry);
    }

    @Captor
    private ArgumentCaptor<LedgerEntry> entryCaptor;

    @Test
    void shouldPersistDebitAndCreditEntriesForPaymentEvent() {
        PaymentEvent event = new PaymentEvent(
                "txn-001",
                "account-123",
                "account-456",
                new BigDecimal("750.00"),
                Instant.parse("2026-01-01T12:00:00Z")
        );

        ledgerService.process(event);

        verify(repository, times(2)).save(entryCaptor.capture());
        assertThat(entryCaptor.getAllValues()).hasSize(2);

        LedgerEntry debitEntry = entryCaptor.getAllValues().get(0);
        LedgerEntry creditEntry = entryCaptor.getAllValues().get(1);

        assertEntry(debitEntry, "account-123", EntryType.DEBIT, new BigDecimal("750.00"));
        assertEntry(creditEntry, "account-456", EntryType.CREDIT, new BigDecimal("750.00"));
    }

    private void assertEntry(LedgerEntry entry, String account, EntryType type, BigDecimal amount) {
        try {
            java.lang.reflect.Field accountField = LedgerEntry.class.getDeclaredField("account");
            java.lang.reflect.Field typeField = LedgerEntry.class.getDeclaredField("type");
            java.lang.reflect.Field amountField = LedgerEntry.class.getDeclaredField("amount");
            accountField.setAccessible(true);
            typeField.setAccessible(true);
            amountField.setAccessible(true);

            assertThat(accountField.get(entry)).isEqualTo(account);
            assertThat(typeField.get(entry)).isEqualTo(type);
            assertThat(amountField.get(entry)).isEqualTo(amount);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
