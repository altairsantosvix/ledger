package com.bank.payment.kafka;

import com.bank.common.kafka.KafkaTopics;
import com.bank.payment.model.PaymentEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Counter paymentEventsPublished;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate, MeterRegistry registry) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentEventsPublished = Counter.builder("payment_events_published_total")
                .description("Total number of payment events published to Kafka")
                .register(registry);
    }

    public void publish(PaymentEvent event) {
        kafkaTemplate.send(
                KafkaTopics.PAYMENTS,
                event.transactionId(),
                event
        );
        paymentEventsPublished.increment();
    }
}