package com.nbicocchi.payment.service;

import com.nbicocchi.payment.persistence.repository.PaymentRepository;
import com.nbicocchi.payment.persistence.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentCommandService {
    private final PaymentRepository paymentRepository;

    public Payment createPayment(String orderId, String creditCardNumber) {
        if (!creditCardNumber.startsWith("7777")) {
            throw new IllegalArgumentException("Credit card not valid.");
        }
        Payment payment = new Payment(orderId, creditCardNumber);
        paymentRepository.save(payment);
        return payment;
    }
}
