package com.bank.payment.service;

import com.bank.payment.kafka.PaymentEventProducer;
import com.bank.payment.model.PaymentEvent;
import com.bank.payment.model.PaymentRequest;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentEventProducer producer;

    private PaymentService paymentService;

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(producer, meterRegistry);
    }

    @Captor
    private ArgumentCaptor<PaymentEvent> eventCaptor;

    @Test
    void shouldPublishPaymentEventWhenRequestIsValid() {
        PaymentRequest request = new PaymentRequest(
                "account-123",
                "account-456",
                new BigDecimal("150.00"),
                "BRL",
                "idem-001"
        );

        paymentService.transfer(request);

        verify(producer).publish(eventCaptor.capture());
        PaymentEvent event = eventCaptor.getValue();

        assertThat(event.transactionId()).isNotBlank();
        assertThat(event.sourceAccount()).isEqualTo(request.sourceAccount());
        assertThat(event.targetAccount()).isEqualTo(request.targetAccount());
        assertThat(event.amount()).isEqualByComparingTo(request.amount());
        assertThat(event.timestamp()).isNotNull();
    }

    @Test
    void shouldRejectPaymentRequestWhenAmountIsZeroOrNegative() {
        PaymentRequest invalidRequest = new PaymentRequest(
                "account-123",
                "account-456",
                BigDecimal.ZERO,
                "BRL",
                "idem-002"
        );

        assertThatThrownBy(() -> paymentService.transfer(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid amount");
    }
}
