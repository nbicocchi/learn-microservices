package com.nbicocchi.payment.events;

import com.nbicocchi.payment.persistence.BankAccountEventStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
@Component
public class EventReceiver {
    private final BankAccountEventStore eventStore;

    /**
     * Receive generic events from RabbitMQ:
     * {
     *   "key": "money.account.created",
     *   "data": { "accountId": "acc445", "amount": 1000.0 },
     *   "eventCreatedAt": "2025-11-18T16:00:00Z"
     * }
     */
    @Bean
    public Consumer<Event<String, AccountEvent>> accountProcessor() {
        return event -> {
            log.info("Received event: {}", event.getKey());
            eventStore.append(event.getData().getAccountId(), event);
            log.info("Stored event: {}", event);
        };
    }
}
