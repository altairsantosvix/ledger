package com.bank.payment.service;

import com.bank.payment.kafka.PaymentEventProducer;
import com.bank.payment.model.PaymentEvent;
import com.bank.payment.model.PaymentRequest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentEventProducer producer;
    private final Counter paymentSuccessCounter;
    private final Counter paymentFailureCounter;
    private final Timer paymentTransferTimer;

    public PaymentService(PaymentEventProducer producer, MeterRegistry registry) {
        this.producer = producer;
        this.paymentSuccessCounter = Counter.builder("payment_requests_total")
                .description("Total number of payment requests processed")
                .tag("result", "success")
                .register(registry);
        this.paymentFailureCounter = Counter.builder("payment_requests_total")
                .description("Total number of payment requests that failed validation")
                .tag("result", "invalid")
                .register(registry);
        this.paymentTransferTimer = Timer.builder("payment_transfer_duration_seconds")
                .description("Time spent creating and publishing payment events")
                .publishPercentiles(0.5, 0.95)
                .register(registry);
    }

    public void transfer(PaymentRequest request) {
        Timer.Sample sample = Timer.start();
        try {
            validate(request);

            PaymentEvent event = new PaymentEvent(
                    UUID.randomUUID().toString(),
                    request.sourceAccount(),
                    request.targetAccount(),
                    request.amount(),
                    Instant.now()
            );

            producer.publish(event);
            paymentSuccessCounter.increment();
        } catch (IllegalArgumentException ex) {
            paymentFailureCounter.increment();
            throw ex;
        } finally {
            sample.stop(paymentTransferTimer);
        }
    }

    private void validate(PaymentRequest request) {
        if (request.amount().signum() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }
}