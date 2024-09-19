package com.baeldung.lsd.worker;

import com.baeldung.lsd.persistence.model.ProductWarehouse;
import com.baeldung.lsd.persistence.repository.ProductWarehouseRepository;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

public class DeleteProductFromWarehouseProduct implements Worker {

    private final String taskDefName;
    private final ProductWarehouseRepository productWarehouseRepository;

    public DeleteProductFromWarehouseProduct(@Value("taskDefName") String taskDefName,  ProductWarehouseRepository productWarehouseRepository) {
        System.out.println("TaskDefName: " + taskDefName);
        this.taskDefName = taskDefName;
        this.productWarehouseRepository = productWarehouseRepository;
    }

    @Override
    public String getTaskDefName() {
        return taskDefName;
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        String code = (String) task.getInputData().get("productCode");

        Optional<ProductWarehouse> productWarehouse = productWarehouseRepository.findByCode(code);

        if(productWarehouse.isPresent()) {
            ProductWarehouse product = productWarehouse.get();

            result.addOutputData("name", product.getName());
            result.addOutputData("description", product.getDescription());

            productWarehouseRepository.delete(product);

            System.out.println("Delete product from warehouse");

            result.setStatus(TaskResult.Status.COMPLETED);
        }
        else {
            result.setStatus(TaskResult.Status.FAILED);
        }

        return result;
    }

}
