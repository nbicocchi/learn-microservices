package com.baeldung.lsd.worker;

import com.baeldung.lsd.persistence.model.ProductCart;
import com.baeldung.lsd.persistence.repository.ProductCartRepository;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

public class CartDeleteWorker implements Worker {

    private final String taskDefName;
    private final ProductCartRepository productChartRepository;

    public CartDeleteWorker(@Value("taskDefName") String taskDefName, ProductCartRepository productWarehouseRepository) {
        System.out.println("TaskDefName: " + taskDefName);
        this.taskDefName = taskDefName;
        this.productChartRepository = productWarehouseRepository;
    }

    @Override
    public String getTaskDefName() {
        return taskDefName;
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        String code = (String) task.getInputData().get("productCode");

        Optional<ProductCart> productWarehouse = productChartRepository.findByCode(code);

        if(productWarehouse.isPresent()) {
            ProductCart product = productWarehouse.get();

            result.addOutputData("name", product.getName());
            result.addOutputData("description", product.getDescription());

            productChartRepository.delete(product);

            System.out.println("Delete product from warehouse");

            result.setStatus(TaskResult.Status.COMPLETED);
        }
        else {
            result.setStatus(TaskResult.Status.FAILED);
        }

        return result;
    }

}
