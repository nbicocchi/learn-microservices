package com.nbicocchi.order.workers;

import com.nbicocchi.order.persistence.model.Order;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class OrderSagaWorkflow {
    private final WorkflowClient workflowClient;
    public Map<String, Object> startOrderFlow(Order order) {
        StartWorkflowRequest request = new StartWorkflowRequest();
        request.setName("order-saga");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("orderId", order.getOrderId());
        inputData.put("productIds", order.getProductIds());
        inputData.put("customerId", order.getCustomerId());
        inputData.put("creditCardNumber", order.getCreditCardNumber());
        inputData.put("status", order.getStatus());
        request.setInput(inputData);
        String workflowId = workflowClient.startWorkflow(request);
        return Map.of("workflowId", workflowId);
    }
}
