package com.nbicocchi.order.service;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.repository.query.OrderQueryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderQueryService {
    private final OrderQueryRepository orderQueryRepository;

    public Order findByOrderId(String orderId) {
        return orderQueryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public List<Order> findAllOrders() { return (List<Order>) orderQueryRepository.findAll(); }
}