package com.nbicocchi.order.controller;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.service.OrderSagaWorkflow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class OrderController {
    private final OrderSagaWorkflow workflowService;

    /**
     * curl -X POST http://localhost:9000/order \
     * -H "Content-Type: application/json" \
     * -d '{
     * "id": null,
     * "orderId": "7fb91305-6799-4d40-a9fc-2bf9086d0100",
     * "productIds": "PROD-A1,PROD-D4",
     * "customerId": "C-001",
     * "creditCardNumber": "7777-1234-5678-0000",
     * "status": "PENDING"
     */

    @PostMapping(value = "/order", produces = "application/json")
    public ResponseEntity<Map<String, Object>> triggerOrderFlow(@RequestBody Order order) {
        log.info("Starting order flow for: {}", order);
        return ResponseEntity.ok(workflowService.startOrderFlow(order));
    }
}
