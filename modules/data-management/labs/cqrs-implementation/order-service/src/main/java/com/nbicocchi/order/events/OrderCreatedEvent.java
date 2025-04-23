package com.nbicocchi.order.events;

import com.nbicocchi.order.persistence.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrderCreatedEvent {
    private final Order order;
}
