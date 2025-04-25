package com.nbicocchi.inventory.service;

import com.nbicocchi.inventory.persistence.model.Inventory;
import com.nbicocchi.inventory.persistence.repository.InventoryRepository;
import com.nbicocchi.inventory.dto.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public boolean inventoryCheck(Order order) {
        List<String> productIds = Arrays.stream(order.getProductIds().split(",")).toList();
        log.info("Verifying inventory {}...", order);
        for (String id : productIds) {
            Optional<Inventory> inventoryOptional = inventoryRepository.findInventoriesByProductId(id);
            if (inventoryOptional.isPresent()) {
                // product found
                Inventory inventory = inventoryOptional.get();
                if (inventory.getQuantity() > 0) {
                    // product found, inventory ok
                    inventory.setQuantity(inventory.getQuantity() - 1);
                    inventoryRepository.save(inventory);
                } else {
                    // product found, inventory empty
                    log.info("Verifying inventory (not valid)");
                    return false;
                }
            } else {
                // product not found!
                log.info("Verifying inventory (not valid)");
                return false;
            }
        }
        log.info("Verifying inventory (valid)");
        return true;
    }
}
