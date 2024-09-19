package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.ProductCart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductCartRepository extends CrudRepository<ProductCart, Long> {
    Optional<ProductCart> findByCode(String code);
}
