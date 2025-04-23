package com.nbicocchi.payment.controller;

import com.nbicocchi.payment.service.PaymentCommandService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
public class PaymentCommandController {
    private final PaymentCommandService paymentCommandService;

    @PostMapping(value = "/payment")
    public ResponseEntity<Boolean> createPayment(
            @RequestParam String orderId,
            @RequestParam String creditCardNumber) {
        log.info("Creating payment for order {} with credit card number {}", orderId, creditCardNumber);
        try {
            paymentCommandService.createPayment(orderId, creditCardNumber);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
