package com.nbicocchi.payment.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbicocchi.payment.projections.AccountProjection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
@Component
public class EventReceiver {
    AccountProjection accountProjection;

    @Bean
    public Consumer<Event<String, Object>> accountProcessor() {
        return event -> {
            log.info("Received event: key={}, data={}", event.getKey(), event.getData());

            switch (event.getKey()) {
                case "money.account.created" -> {
                    AccountCreated ac = new ObjectMapper()
                            .convertValue(event.getData(), AccountCreated.class);
                    log.info("AccountCreated event: {}", ac);
                    accountProjection.createAccount(ac.getAccountId(), ac.getAmount());
                }
                case "money.deposit" -> {
                    MoneyDeposited md = new ObjectMapper()
                            .convertValue(event.getData(), MoneyDeposited.class);
                    log.info("MoneyDeposited event: {}", md);
                    accountProjection.apply(md.getAccountId(), md.getAmount());
                }
                case "money.withdraw" -> {
                    MoneyWithdrawn mw = new ObjectMapper()
                            .convertValue(event.getData(), MoneyWithdrawn.class);
                    log.info("MoneyWithdrawn event: {}", mw);
                    accountProjection.apply(mw.getAccountId(), -mw.getAmount());
                }
                default -> log.warn("Unknown event: {}", event.getKey());
            }
        };
    }

}
