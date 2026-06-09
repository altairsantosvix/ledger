package com.bank.ledger.service;

import com.bank.ledger.model.EntryType;
import com.bank.ledger.model.LedgerEntry;
import com.bank.ledger.repository.LedgerRepository;
import com.bank.payment.model.PaymentEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {

    private final LedgerRepository repository;
    private final Counter paymentEventsProcessed;
    private final Counter ledgerEntriesCreated;
    private final Timer ledgerProcessingTimer;

    public LedgerService(LedgerRepository repository, MeterRegistry registry) {
        this.repository = repository;
        this.paymentEventsProcessed = Counter.builder("ledger_payment_events_processed_total")
                .description("Total number of payment events processed by ledger service")
                .register(registry);
        this.ledgerEntriesCreated = Counter.builder("ledger_entries_created_total")
                .description("Total number of ledger entries created")
                .register(registry);
        this.ledgerProcessingTimer = Timer.builder("ledger_processing_duration_seconds")
                .description("Time spent processing payment events in the ledger service")
                .publishPercentiles(0.5, 0.95)
                .register(registry);
    }

    @Transactional
    public void process(PaymentEvent event) {
        Timer.Sample sample = Timer.start();
        try {
            LedgerEntry debit = new LedgerEntry(
                    event.transactionId(),
                    event.sourceAccount(),
                    EntryType.DEBIT,
                    event.amount(),
                    event.timestamp()
            );

            LedgerEntry credit = new LedgerEntry(
                    event.transactionId(),
                    event.targetAccount(),
                    EntryType.CREDIT,
                    event.amount(),
                    event.timestamp()
            );

            repository.save(debit);
            repository.save(credit);
            paymentEventsProcessed.increment();
            ledgerEntriesCreated.increment(2);
        } finally {
            sample.stop(ledgerProcessingTimer);
        }
    }
}