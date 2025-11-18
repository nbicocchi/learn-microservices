package com.nbicocchi.payment.projections;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Data
public class AccountProjection {
    private final ConcurrentMap<String, AccountView> accounts = new ConcurrentHashMap<>();

    @Data
    public static class AccountView {
        private String accountId;
        private double balance;

        public AccountView(String accountId, double balance) {
            this.accountId = accountId;
            this.balance = balance;
        }
    }

    public void apply(String accountId, double delta) {
        accounts.compute(accountId, (k, v) -> {
            if (v == null) v = new AccountView(accountId, 0);
            v.setBalance(v.getBalance() + delta);
            return v;
        });
    }

    public void createAccount(String accountId, double initialBalance) {
        accounts.putIfAbsent(accountId, new AccountView(accountId, initialBalance));
    }
}

