package com.nbicocchi.payment.worker;

import com.nbicocchi.payment.dto.Order;
import com.nbicocchi.payment.persistence.model.Payment;
import com.nbicocchi.payment.persistence.repository.PaymentRepository;
import com.nbicocchi.payment.dto.TaskResult;
import com.nbicocchi.payment.service.CardValidatorService;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class PaymentWorkers {
    CardValidatorService cardValidatorService;

    @WorkerTask(value = "payment-check", threadCount = 1, pollingInterval = 200)
    public TaskResult paymentCheck(Order order) {
        if (cardValidatorService.paymentCheck(order)) {
            return new TaskResult(TaskResult.Result.PASS, "");
        } else {
            return new TaskResult(TaskResult.Result.FAIL, "");
        }
    }
}
