package com.bank.fraud.service;

import com.bank.fraud.model.FraudAlert;
import com.bank.fraud.publisher.FraudAlertPublisher;
import com.bank.payment.model.PaymentEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FraudDetectionService {

    private final FraudAlertPublisher publisher;
    private final Counter paymentEventsAnalyzed;
    private final Counter fraudAlertsPublished;
    private final Gauge knownRecipientsGauge;

    // simula base de "recipients novos"
    private final Set<String> knownAccounts = ConcurrentHashMap.newKeySet();

    public FraudDetectionService(FraudAlertPublisher publisher, MeterRegistry registry) {
        this.publisher = publisher;
        this.paymentEventsAnalyzed = Counter.builder("fraud_payment_events_analyzed_total")
                .description("Total number of payment events analyzed by fraud service")
                .register(registry);
        this.fraudAlertsPublished = Counter.builder("fraud_alerts_created_total")
                .description("Total number of fraud alerts created by fraud service")
                .register(registry);
        this.knownRecipientsGauge = Gauge.builder("fraud_known_recipient_accounts", knownAccounts, Set::size)
                .description("Number of unique recipient accounts observed by fraud detection")
                .register(registry);
    }

    public void analyze(PaymentEvent event) {
        paymentEventsAnalyzed.increment();

        boolean isNewRecipient = !knownAccounts.contains(event.targetAccount());
        boolean highValue = event.amount().compareTo(new BigDecimal("5000")) > 0;

        if (isNewRecipient) {
            knownAccounts.add(event.targetAccount());
        }

        if (highValue && isNewRecipient) {
            FraudAlert alert = new FraudAlert(
                    event.transactionId(),
                    event.sourceAccount(),
                    event.targetAccount(),
                    event.amount(),
                    "HIGH_RISK_NEW_RECIPIENT"
            );

            publisher.publish(alert);
            fraudAlertsPublished.increment();
        }
    }
}