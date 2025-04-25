package com.nbicocchi.order_read.controller;

import com.nbicocchi.order_read.persistence.model.OrderRead;
import com.nbicocchi.order_read.persistence.repository.OrderReadRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@RestController
public class OrderReadController {
    OrderReadRepository orderReadRepository;

    /**
     * curl -X GET "http://localhost:9003/order?customerId=C-001&year=2025&month=4" -H "Accept: application/json"
     */

    @GetMapping(value = "/order", produces = "application/json")
    public ResponseEntity<OrderRead> triggerOrderFlow(@RequestParam String customerId, @RequestParam Integer year, @RequestParam Integer month) {
        Optional<OrderRead> orderRead = orderReadRepository.findByCustomerIdAndMAndY(customerId, month, year);
        return ResponseEntity.ok(orderRead.orElse(null));
    }
}
