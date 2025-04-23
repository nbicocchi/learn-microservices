package com.nbicocchi.order.events;

import com.nbicocchi.order.persistence.repository.query.OrderQueryRepository;
import com.nbicocchi.order.persistence.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class OrderEventHandler {
    private final OrderQueryRepository orderQueryRepository;

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        orderQueryRepository.save(order);
    }

    @EventListener
    public void handleOrderDeleted(OrderDeletedEvent event) {
        String orderId = event.getOrderId();
        orderQueryRepository.findByOrderId(orderId).ifPresent(orderQueryRepository::delete);
    }
}