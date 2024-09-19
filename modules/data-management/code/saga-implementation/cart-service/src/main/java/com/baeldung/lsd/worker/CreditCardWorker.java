package com.baeldung.lsd.worker;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.springframework.beans.factory.annotation.Value;

public class CreditCardWorker implements Worker {

    private final String taskDefName;

    public CreditCardWorker(@Value("taskDefName") String taskDefName) {
        System.out.println("TaskDefName: " + taskDefName);
        this.taskDefName = taskDefName;
    }

    @Override
    public String getTaskDefName() {
        return taskDefName;
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        String creditCard = (String) task.getInputData().get("creditCard");

        System.out.println("Credit card: " + creditCard);

        if (creditCard != null && creditCard.matches("^\\d{16}$")) {
            result.addOutputData("status", "valid");
        } else {
            result.addOutputData("status", "Invalid credit card");
        }

        result.setStatus(TaskResult.Status.COMPLETED);
        System.out.println("Controllo numero di carta di credito");
        return result;
    }

}
