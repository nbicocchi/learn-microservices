package com.nbicocchi.order.controller;

import com.nbicocchi.order.dto.OrderDto;
import com.nbicocchi.order.integration.ProductIntegration;
import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.model.ProductOrder;
import com.nbicocchi.order.persistence.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    OrderRepository orderRepository;
    ProductIntegration productIntegration;

    public OrderController(OrderRepository orderRepository, ProductIntegration productIntegration) {
        this.orderRepository = orderRepository;
        this.productIntegration = productIntegration;
    }

    @GetMapping(value = "")
    public Iterable<OrderDto> findAll() {
        Iterable<Order> orders = orderRepository.findAll();

        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderDto orderDto = new OrderDto(
                    order.getId(),
                    order.getUuid(),
                    order.getTimestamp(),
                    new HashSet<>()
            );

            for (ProductOrder productOrder : order.getProducts()) {
                orderDto.getProducts().add(productIntegration.findbyUuid(productOrder.getUuid()));
            }
            orderDtos.add(orderDto);
        }
        return orderDtos;
    }

    @GetMapping(value = "/{id}")
    public Order findById(@PathVariable Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}