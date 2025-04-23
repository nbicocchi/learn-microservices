package com.nbicocchi.order.controller;

import com.nbicocchi.order.service.OrderQueryService;
import com.nbicocchi.order.persistence.model.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class OrderQueryController {
    private final OrderQueryService orderQueryService;

    @GetMapping(value = "/order", produces = "application/json")
    public ResponseEntity<List<Order>> returnAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderQueryService.findAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/order/{orderId}", produces = "application/json")
    public ResponseEntity<Order> returnOrderById(@PathVariable String orderId) {
        log.info("Fetching order with id {}", orderId);
        Order order = orderQueryService.findByOrderId(orderId);
        return ResponseEntity.ok(order);
    }
}
