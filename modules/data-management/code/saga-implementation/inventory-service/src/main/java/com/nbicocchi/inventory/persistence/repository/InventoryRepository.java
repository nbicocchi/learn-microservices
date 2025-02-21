package com.nbicocchi.inventory.persistence.repository;

import com.nbicocchi.inventory.persistence.model.Inventory;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InventoryRepository extends CrudRepository<Inventory, Long> {
    Optional<Inventory> findInventoriesByProductId(String productId);
}
