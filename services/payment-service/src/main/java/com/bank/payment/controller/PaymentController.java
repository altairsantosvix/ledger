package com.bank.payment.controller;

import com.bank.payment.model.PaymentRequest;
import com.bank.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> pay(@RequestBody PaymentRequest request) {
        service.transfer(request);
        return ResponseEntity.accepted().build();
    }
}