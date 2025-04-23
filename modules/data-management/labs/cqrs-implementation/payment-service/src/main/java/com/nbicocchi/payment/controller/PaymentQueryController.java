package com.nbicocchi.payment.controller;

import com.nbicocchi.payment.service.PaymentQueryService;
import com.nbicocchi.payment.persistence.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class PaymentQueryController {
    private final PaymentQueryService paymentQueryService;

    @GetMapping("/payment/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable String orderId) {
        Payment payment = paymentQueryService.findPaymentById(orderId);
        return ResponseEntity.ok(payment);
    }
}
