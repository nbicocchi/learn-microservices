package com.nbicocchi.inventory.service;

import com.nbicocchi.inventory.persistence.repository.InventoryRepository;
import com.nbicocchi.inventory.persistence.model.Inventory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class InventoryCommandService {
    private final InventoryRepository inventoryRepository;

    public Inventory updateInventory(String productId) {
        return inventoryRepository.findInventoriesByProductId(productId)
                .map(inventory -> {
                    if (inventory.getQuantity() > 0) {
                        inventory.setQuantity(inventory.getQuantity() - 1);
                        return inventoryRepository.save(inventory);
                    } else {
                        throw new IllegalArgumentException("Not enough inventory for product: " + productId);
                    }
                }).orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }
}
