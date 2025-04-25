package com.nbicocchi.order.events;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Log4j2
@Component
public class EventReceiver {
    OrderService orderService;

    @Bean
    public Consumer<Event<String, Order>> sagaProcessor() {
        return event -> {
            log.info("Processing event: " + event);
            switch (event.getKey()) {
                case "payment.invalid" -> managePaymentInvalid(event.getData());
                case "inventory.invalid" -> manageInventoryInvalid(event.getData());
                case "inventory.valid" -> manageInventoryValid(event.getData());
            }
        };
    }

    private void managePaymentInvalid(Order order) {
        orderService.deletePendingOrder(order);
    }

    private void manageInventoryInvalid(Order order) {
        orderService.deletePendingOrder(order);
    }

    private void manageInventoryValid(Order order) {
        orderService.confirmPendingOrder(order);
    }
}