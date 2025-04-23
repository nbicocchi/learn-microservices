package com.nbicocchi.inventory.controller;

import com.nbicocchi.inventory.service.InventoryQueryService;
import com.nbicocchi.inventory.persistence.model.Inventory;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class InventoryQueryController {
    private final InventoryQueryService inventoryQueryService;

    @GetMapping(value = "/inventory")
    public ResponseEntity<List<Inventory>> returnAllProducts() {
        return ResponseEntity.ok(inventoryQueryService.findAllProducts());
    }

    @GetMapping(value = "/inventory/{productId}")
    public ResponseEntity<Inventory> returnProductById(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryQueryService.findProductById(productId));
    }
}
