package com.nbicocchi.order.persistence.repository;

import com.nbicocchi.order.persistence.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
