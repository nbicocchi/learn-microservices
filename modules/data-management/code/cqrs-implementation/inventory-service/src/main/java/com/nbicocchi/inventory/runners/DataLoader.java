package com.nbicocchi.inventory.runners;

import com.nbicocchi.inventory.persistence.model.Inventory;
import com.nbicocchi.inventory.persistence.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {
    InventoryRepository inventoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        inventoryRepository.save(new Inventory("P-001", 9));
        inventoryRepository.save(new Inventory("P-002", 0));
        inventoryRepository.save(new Inventory("P-003", 1));
    }
}
