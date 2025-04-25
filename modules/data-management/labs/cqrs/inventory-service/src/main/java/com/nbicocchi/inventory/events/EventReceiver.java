package com.nbicocchi.inventory.events;

import com.nbicocchi.inventory.dto.Order;
import com.nbicocchi.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Log4j2
@Component
public class EventReceiver {
    InventoryService inventoryService;
    EventSender eventSender;

    @Bean
    public Consumer<Event<String, Order>> sagaProcessor() {
        return event -> {
            log.info("Processing event: " + event);
            if (event.getKey().equals("payment.valid")) {
                manageInventory(event.getData());
            }
        };
    }

    private void manageInventory(Order order) {
        if (inventoryService.inventoryCheck(order)) {
            eventSender.send(
                    "sagaProcessor-out-0",
                    "inventory.valid",
                    new Event<>("inventory.valid", order));
        } else {
            eventSender.send(
                    "sagaProcessor-out-0",
                    "inventory.invalid",
                    new Event<>("inventory.invalid", order));
        }
    }
}