package com.nbicocchi.payment.persistence.repository;

import com.nbicocchi.payment.persistence.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
