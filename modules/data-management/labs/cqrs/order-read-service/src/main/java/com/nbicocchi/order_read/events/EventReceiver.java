package com.nbicocchi.order_read.events;

import com.nbicocchi.order_read.dto.Order;
import com.nbicocchi.order_read.service.OrderReadService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Log4j2
@Component
public class EventReceiver {
    OrderReadService orderReadService;

    @Bean
    public Consumer<Event<String, Order>> sagaProcessor() {
        return event -> {
            log.info("Processing event: " + event);
            switch (event.getKey()) {
                case "order.confirmed" -> updateReadModel(event.getData());
            }
        };
    }

    private void updateReadModel(Order order) {
        orderReadService.updateReadModel(order);
    }
}