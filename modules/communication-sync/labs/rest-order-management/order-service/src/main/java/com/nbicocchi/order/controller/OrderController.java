package com.nbicocchi.order.controller;

import com.nbicocchi.order.dto.OrderDto;
import com.nbicocchi.order.dto.OrderLineDto;
import com.nbicocchi.order.dto.ProductDto;
import com.nbicocchi.order.integration.ProductIntegration;
import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.model.OrderLine;
import com.nbicocchi.order.persistence.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    OrderRepository orderRepository;
    ProductIntegration productIntegration;

    public OrderController(OrderRepository orderRepository, ProductIntegration productIntegration) {
        this.orderRepository = orderRepository;
        this.productIntegration = productIntegration;
    }

    @GetMapping(value = "/local/{id}")
    public Order findById(@PathVariable Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @GetMapping(value = "/remote/{id}")
    public OrderDto findByIdWithRemoteCall(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.orElseThrow(() -> new RuntimeException("Order not found"));

        Order foundOrder = optionalOrder.get();
        OrderDto orderDto = new OrderDto(
                foundOrder.getUuid(),
                foundOrder.getTimestamp(),
                new HashSet<>()
        );

        for (OrderLine orderLine : foundOrder.getOrderLines()) {
            ProductDto product = productIntegration.findbyUuid(orderLine.getUuid());
            orderDto.getOrderLineDtos().add(
                    new OrderLineDto(
                            product.getUuid(),
                            product.getName(),
                            product.getWeight(),
                            orderLine.getAmount()));
        }
        return orderDto;
    }
}