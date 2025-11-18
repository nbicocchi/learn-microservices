package com.nbicocchi.payment.commands;

public record DepositMoney(String accountId, double amount) implements BankCommand {}

