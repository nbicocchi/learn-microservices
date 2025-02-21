package com.nbicocchi.order.persistence.repository.command;


import com.nbicocchi.order.persistence.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderCommandRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
}
