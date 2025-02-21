package com.nbicocchi.order.controller;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.service.OrderCommandService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class OrderCommandController {
    private final OrderCommandService orderCommandService;

    @PostMapping(value = "/order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderCommandService.createOrder(
                order.getProductId(),
                order.getCustomerId(),
                order.getCreditCardNumber()
        );
        return ResponseEntity.ok(createdOrder);
    }

    @DeleteMapping(value = "/order/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable String orderId) {
        orderCommandService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
