package com.nbicocchi.order.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrderDeletedEvent {
    private final String orderId;
}
