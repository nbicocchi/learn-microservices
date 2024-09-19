package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.ProductWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductWarehouseRepository extends JpaRepository<ProductWarehouse, Long> {
    Optional<ProductWarehouse> findByCode(String code);
}
