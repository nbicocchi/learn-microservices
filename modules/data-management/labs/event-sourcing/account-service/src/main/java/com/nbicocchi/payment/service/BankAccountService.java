package com.nbicocchi.payment.service;

import com.nbicocchi.payment.commands.CreateAccount;
import com.nbicocchi.payment.commands.DepositMoney;
import com.nbicocchi.payment.commands.WithdrawMoney;
import com.nbicocchi.payment.events.AccountCreated;
import com.nbicocchi.payment.events.MoneyDeposited;
import com.nbicocchi.payment.events.MoneyWithdrawn;
import com.nbicocchi.payment.events.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class BankAccountService {

    /**
     * Handle CreateAccount command → produce AccountCreated event
     */
    public Event<String, AccountCreated> handleAccountCreated(CreateAccount cmd) {
        AccountCreated event = new AccountCreated(cmd.accountId(), cmd.amount());

        log.info("Domain event generated: {}", event);

        return new Event<>("money.account.created", event);
    }

    /**
     * Handle DepositMoney command → produce MoneyDeposited event
     */
    public Event<String, MoneyDeposited> handleDeposit(DepositMoney cmd) {
        MoneyDeposited event = new MoneyDeposited(cmd.accountId(), cmd.amount());

        log.info("Domain event generated: {}", event);

        return new Event<>("money.deposit", event);
    }

    /**
     * Handle WithdrawMoney command → produce MoneyWithdrawn event
     */
    public Event<String, MoneyWithdrawn> handleWithdraw(WithdrawMoney cmd) {
        MoneyWithdrawn event = new MoneyWithdrawn(cmd.accountId(), cmd.amount());

        log.info("Domain event generated: {}", event);

        return new Event<>("money.withdraw", event);
    }
}