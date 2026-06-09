package com.bank.fraud.service;

import com.bank.fraud.model.FraudAlert;
import com.bank.fraud.publisher.FraudAlertPublisher;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private FraudAlertPublisher publisher;

    private FraudDetectionService fraudDetectionService;

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Captor
    private ArgumentCaptor<FraudAlert> alertCaptor;

    private PaymentEvent event;

    @BeforeEach
    void setUp() {
        fraudDetectionService = new FraudDetectionService(publisher, meterRegistry);
        event = new PaymentEvent(
                "txn-123",
                "payer-001",
                "receiver-001",
                new BigDecimal("6000"),
                Instant.parse("2026-01-01T12:00:00Z")
        );
    }

    @Test
    void shouldPublishAlertForHighValueNewRecipient() {
        fraudDetectionService.analyze(event);

        verify(publisher).publish(alertCaptor.capture());
        FraudAlert alert = alertCaptor.getValue();

        assertThat(alert.transactionId()).isEqualTo(event.transactionId());
        assertThat(alert.sourceAccount()).isEqualTo(event.sourceAccount());
        assertThat(alert.targetAccount()).isEqualTo(event.targetAccount());
        assertThat(alert.amount()).isEqualByComparingTo(event.amount());
        assertThat(alert.reason()).isEqualTo("HIGH_RISK_NEW_RECIPIENT");
    }

    @Test
    void shouldNotPublishAlertForKnownRecipient() {
        fraudDetectionService.analyze(event);
        fraudDetectionService.analyze(event);

        verify(publisher).publish(alertCaptor.capture());
        assertThat(alertCaptor.getAllValues()).hasSize(1);
    }

    @Test
    void shouldNotPublishAlertForLowValuePayment() {
        PaymentEvent lowValueEvent = new PaymentEvent(
                "txn-124",
                "payer-001",
                "receiver-002",
                new BigDecimal("1000"),
                Instant.parse("2026-01-01T12:00:00Z")
        );

        fraudDetectionService.analyze(lowValueEvent);

        verify(publisher, never()).publish(any());
    }
}
