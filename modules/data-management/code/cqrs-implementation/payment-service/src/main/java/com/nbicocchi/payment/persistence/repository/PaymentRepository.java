package com.nbicocchi.payment.persistence.repository;

import com.nbicocchi.payment.persistence.model.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
}
