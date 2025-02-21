package com.nbicocchi.order.controller;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.workers.OrderSagaWorkflow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class OrderController {
    private final OrderSagaWorkflow workflowService;

    @PostMapping(value = "/order", produces = "application/json")
    public ResponseEntity<Map<String, Object>> triggerOrderFlow(@RequestBody Order order) {
        log.info("Starting order flow for: {}", order);
        return ResponseEntity.ok(workflowService.startOrderFlow(order));
    }

    @GetMapping(value = "/order", produces = "application/json")
    public Order returnOrderInBrowser() {
        return new Order("P-001,P-002", "C-001", "7777-1234-5678-0000");
    }
}
