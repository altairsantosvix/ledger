package com.bank.ledger.consumer;

import com.bank.common.kafka.KafkaTopics;
import com.bank.ledger.service.LedgerService;
import com.bank.payment.model.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private final LedgerService ledgerService;

    public PaymentEventConsumer(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENTS,
            groupId = "ledger-group"
    )
    public void consume(PaymentEvent event) {

        ledgerService.process(event);
    }
}