package com.nbicocchi.payment.controller;

import com.nbicocchi.payment.projections.AccountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountQueryController {

    private final AccountProjection projection;

    @GetMapping("/{accountId}")
    public AccountProjection.AccountView getAccount(@PathVariable String accountId) {
        return projection.getAccounts().get(accountId);
    }

    @GetMapping
    public Collection<AccountProjection.AccountView> getAllAccounts() {
        return projection.getAccounts().values();
    }
}
