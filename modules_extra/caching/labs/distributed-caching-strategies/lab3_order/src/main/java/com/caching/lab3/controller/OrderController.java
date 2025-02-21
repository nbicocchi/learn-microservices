package com.caching.lab3.controller;

import com.caching.lab3.User;
import com.caching.lab3.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders/{userId}")
    public User getUser(@PathVariable String userId) {
        return orderService.getUserById(userId);
    }
}
