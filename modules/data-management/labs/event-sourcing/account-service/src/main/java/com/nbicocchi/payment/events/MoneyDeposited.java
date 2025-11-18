package com.nbicocchi.payment.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoneyDeposited {
    private String accountId;
    private double amount;
}