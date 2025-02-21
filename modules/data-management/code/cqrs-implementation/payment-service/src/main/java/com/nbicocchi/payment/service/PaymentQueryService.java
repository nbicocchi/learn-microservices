package com.nbicocchi.payment.service;

import com.nbicocchi.payment.persistence.model.Payment;
import com.nbicocchi.payment.persistence.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentQueryService {
    private final PaymentRepository paymentRepository;

    public Payment findPaymentById(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found " + orderId));
    }
}
