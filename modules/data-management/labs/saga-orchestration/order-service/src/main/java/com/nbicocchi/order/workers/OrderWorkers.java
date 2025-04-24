package com.nbicocchi.order.service;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.repository.OrderRepository;
import com.nbicocchi.order.pojos.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
@Slf4j
public class OrderWorkers {
    private final OrderRepository orderRepository;

    @WorkerTask(value = "persist-pending-order", threadCount = 1, pollingInterval = 200)
    public TaskResult placePendingOrder(Order order) {
        log.info("persisting {}...", order);
        Optional<Order> existingOrder = orderRepository.findByOrderId(order.getOrderId());
        if (existingOrder.isPresent()) {
            return new TaskResult(TaskResult.Result.FAIL, "Duplicate order");
        }
        orderRepository.save(order);
        return new TaskResult(TaskResult.Result.PASS, "");
    }

    @WorkerTask(value = "delete-pending-order", threadCount = 1, pollingInterval = 200)
    public TaskResult deletePendingOrder(Order order) {
        log.info("rejecting {}...", order);
        Optional<Order> existingOrder = orderRepository.findByOrderId(order.getOrderId());
        if (existingOrder.isPresent()) {
            Order existing = existingOrder.get();
            existing.setStatus(Order.OrderStatus.REJECTED);
            orderRepository.save(existing);
        }
        return new TaskResult(TaskResult.Result.PASS, "");
    }

    @WorkerTask(value = "confirm-pending-order", threadCount = 1, pollingInterval = 200)
    public TaskResult confirmPendingOrder(Order order) {
        log.info("confirming {}...", order);
        Optional<Order> existingOrder = orderRepository.findByOrderId(order.getOrderId());
        if (existingOrder.isPresent()) {
            Order existing = existingOrder.get();
            existing.setStatus(Order.OrderStatus.APPROVED);
            orderRepository.save(existing);
        }
        return new TaskResult(TaskResult.Result.PASS, "");
    }
}
