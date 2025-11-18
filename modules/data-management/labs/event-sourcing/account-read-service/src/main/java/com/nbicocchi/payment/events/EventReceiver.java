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
    public Consumer<Event<String, AccountEvent>> accountProcessor() {
        return event -> {
            log.info("Received event: key={}, data={}", event.getKey(), event.getData());

            String accountId = event.getData().getAccountId();
            double amount = event.getData().getAmount();

            switch (event.getKey()) {
                case "money.account.created" -> {
                    accountProjection.createAccount(accountId, amount);
                }
                case "money.deposit" -> {
                    accountProjection.apply(accountId, amount);
                }
                case "money.withdraw" -> {
                    accountProjection.apply(accountId, amount);
                }
                default -> log.warn("Unknown event: {}", event.getKey());
            }
        };
    }

}
