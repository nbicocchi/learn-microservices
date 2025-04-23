package com.nbicocchi.order.persistence.repository.query;


import com.nbicocchi.order.persistence.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderQueryRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
}
