package com.nbicocchi.inventory.controller;

import com.nbicocchi.inventory.service.InventoryCommandService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class InventoryCommandController {
    private final InventoryCommandService inventoryCommandService;

    @PutMapping(value = "/inventory/{productId}")
    public ResponseEntity<Boolean> updateInventory(@PathVariable String productId) {
        log.info("Updating {} inventory", productId);
        try {
            inventoryCommandService.updateInventory(productId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
