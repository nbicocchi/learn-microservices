package com.nbicocchi.payment.commands;

public record CreateAccount(String accountId, double amount) implements BankCommand {}

