package com.nbicocchi.order.service;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    public void placePendingOrder(Order order) {
        log.info("persisting {}...", order);
        Optional<Order> existingOrder = orderRepository.findByOrderId(order.getOrderId());
        if (!existingOrder.isPresent()) {
            orderRepository.save(order);
        }
    }

    public void deletePendingOrder(Order order) {
        log.info("rejecting {}...", order);
        Optional<Order> existingOrder = orderRepository.findByOrderId(order.getOrderId());
        if (existingOrder.isPresent()) {
            Order existing = existingOrder.get();
            existing.setStatus(Order.OrderStatus.REJECTED);
            orderRepository.save(existing);
        }
    }

    public void confirmPendingOrder(Order order) {
        log.info("confirming {}...", order);
        Optional<Order> existingOrder = orderRepository.findByOrderId(order.getOrderId());
        if (existingOrder.isPresent()) {
            Order existing = existingOrder.get();
            existing.setStatus(Order.OrderStatus.APPROVED);
            orderRepository.save(existing);
        }
    }
}
