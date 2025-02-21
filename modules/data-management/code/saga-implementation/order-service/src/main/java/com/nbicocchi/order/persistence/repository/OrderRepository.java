package com.nbicocchi.order.persistence.repository;

import com.nbicocchi.order.persistence.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
}
