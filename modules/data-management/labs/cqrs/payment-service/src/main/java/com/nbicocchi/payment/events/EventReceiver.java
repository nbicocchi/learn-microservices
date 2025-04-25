package com.nbicocchi.payment.events;

import com.nbicocchi.payment.dto.Order;
import com.nbicocchi.payment.service.CardValidatorService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Log4j2
@Component
public class EventReceiver {
    CardValidatorService cardValidatorService;
    EventSender eventSender;

    @Bean
    public Consumer<Event<String, Order>> sagaProcessor() {
        return event -> {
            log.info("Processing event: " + event);
            if (event.getKey().equals("order.created")) {
                manageOrderCreated(event.getData());
            }
        };
    }

    private void manageOrderCreated(Order order) {
        if (cardValidatorService.paymentCheck(order)) {
            eventSender.send(
                    "sagaProcessor-out-0",
                    "payment.valid",
                    new Event<>("payment.valid", order));
        } else {
            eventSender.send(
                    "sagaProcessor-out-0",
                    "payment.invalid",
                    new Event<>("payment.invalid", order));
        }
    }
}