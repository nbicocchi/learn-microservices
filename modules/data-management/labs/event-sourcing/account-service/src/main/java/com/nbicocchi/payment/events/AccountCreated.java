package com.nbicocchi.payment.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountCreated {
    private String accountId;
    private double initialBalance;
}
