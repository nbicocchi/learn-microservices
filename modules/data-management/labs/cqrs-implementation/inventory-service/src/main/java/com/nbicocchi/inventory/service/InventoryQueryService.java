package com.nbicocchi.inventory.service;

import com.nbicocchi.inventory.persistence.model.Inventory;
import com.nbicocchi.inventory.persistence.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class InventoryQueryService {
    private final InventoryRepository inventoryRepository;

    public List<Inventory> findAllProducts() { return (List<Inventory>) inventoryRepository.findAll(); }

    public Inventory findProductById(String productId) {
        return inventoryRepository.findInventoriesByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }
}
