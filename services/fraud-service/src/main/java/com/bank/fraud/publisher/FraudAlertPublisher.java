package com.bank.fraud.publisher;

import com.bank.common.kafka.KafkaTopics;
import com.bank.fraud.model.FraudAlert;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FraudAlertPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Counter fraudAlertsPublishedCounter;

    public FraudAlertPublisher(KafkaTemplate<String, Object> kafkaTemplate, MeterRegistry registry) {
        this.kafkaTemplate = kafkaTemplate;
        this.fraudAlertsPublishedCounter = Counter.builder("fraud_alerts_published_total")
                .description("Total number of fraud alerts published to Kafka")
                .register(registry);
    }

    public void publish(FraudAlert alert) {
        kafkaTemplate.send(KafkaTopics.FRAUD_ALERTS, alert.transactionId(), alert);
        fraudAlertsPublishedCounter.increment();
    }
}
