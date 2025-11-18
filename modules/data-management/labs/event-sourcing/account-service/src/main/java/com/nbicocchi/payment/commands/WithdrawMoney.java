package com.nbicocchi.payment.commands;

public record WithdrawMoney(String accountId, double amount) implements BankCommand {}

