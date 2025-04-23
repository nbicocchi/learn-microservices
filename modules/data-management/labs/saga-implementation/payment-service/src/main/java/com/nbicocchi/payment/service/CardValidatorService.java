package com.nbicocchi.payment.service;

import org.springframework.stereotype.Service;

@Service
public class CardValidatorService {
    public boolean validateCard(String cardNumber) {
        return cardNumber.startsWith("7777");
    }
}
