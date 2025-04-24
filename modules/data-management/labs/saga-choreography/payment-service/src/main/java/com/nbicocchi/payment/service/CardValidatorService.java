package com.nbicocchi.payment.service;

import com.nbicocchi.payment.persistence.model.Payment;
import com.nbicocchi.payment.persistence.repository.PaymentRepository;
import com.nbicocchi.payment.pojos.Order;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Log4j2
@Service
public class CardValidatorService {
    PaymentRepository paymentRepository;

    public boolean paymentCheck(Order order) {
        log.info("Verifying {}...", order);
        Payment payment = new Payment(order.getOrderId(), order.getCreditCardNumber());
        if (validateCard(order.getCreditCardNumber())) {
            log.info("Verifying Order(valid)");
            payment.setSuccess(Boolean.TRUE);
            paymentRepository.save(payment);
            return true;
        }
        log.info("Verifying Order(not valid)");
        payment.setSuccess(Boolean.FALSE);
        paymentRepository.save(payment);
        return false;
    }

    private boolean validateCard(String cardNumber) {
        return cardNumber.startsWith("7777");
    }
}
