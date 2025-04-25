package com.nbicocchi.inventory.workers;

import com.nbicocchi.inventory.dto.Order;
import com.nbicocchi.inventory.dto.TaskResult;
import com.nbicocchi.inventory.service.InventoryService;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class InventoryWorkers {
    private final InventoryService inventoryService;

    @WorkerTask(value = "inventory-check", threadCount = 1, pollingInterval = 200)
    public TaskResult inventoryCheck(Order order) {
        if (inventoryService.inventoryCheck(order)) {
            return new TaskResult(TaskResult.Result.PASS, "");
        } else {
            return new TaskResult(TaskResult.Result.FAIL, "");
        }
    }
}
