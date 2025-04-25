package com.nbicocchi.order.workers;

import com.nbicocchi.order.dto.TaskResult;
import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.service.OrderService;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
@Slf4j
public class OrderWorkers {
    private final OrderService orderService;
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

    @WorkerTask(value = "persist-pending-order", threadCount = 1, pollingInterval = 200)
    public TaskResult placePendingOrder(Order order) {
        orderService.placePendingOrder(order);
        return new TaskResult(TaskResult.Result.PASS, "");
    }

    @WorkerTask(value = "delete-pending-order", threadCount = 1, pollingInterval = 200)
    public TaskResult deletePendingOrder(Order order) {
        orderService.deletePendingOrder(order);
        return new TaskResult(TaskResult.Result.PASS, "");
    }

    @WorkerTask(value = "confirm-pending-order", threadCount = 1, pollingInterval = 200)
    public TaskResult confirmPendingOrder(Order order) {
        orderService.confirmPendingOrder(order);
        return new TaskResult(TaskResult.Result.PASS, "");
    }
}
