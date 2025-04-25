package com.nbicocchi.order_read.persistence.repository;

import com.nbicocchi.order_read.persistence.model.OrderRead;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderReadRepository extends CrudRepository<OrderRead, Long> {
    Optional<OrderRead> findByCustomerIdAndMAndY(String customerId, Integer m, Integer y);
}
