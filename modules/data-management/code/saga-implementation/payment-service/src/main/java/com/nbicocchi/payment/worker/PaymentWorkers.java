package com.nbicocchi.payment.worker;

import com.nbicocchi.payment.pojos.Order;
import com.nbicocchi.payment.persistence.model.Payment;
import com.nbicocchi.payment.persistence.repository.PaymentRepository;
import com.nbicocchi.payment.pojos.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class PaymentWorkers {
    PaymentRepository paymentRepository;

    /**
     * Note: Using this setting, up to 5 tasks will run in parallel, with tasks being polled every 200ms
     */
    @WorkerTask(value = "payment-check", threadCount = 1, pollingInterval = 200)
    public TaskResult paymentCheck(Order order) {
        log.info("Verifying {}...", order);
        Payment payment = new Payment(order.getOrderId(), order.getCreditCardNumber());
        if (payment.getCreditCardNumber().startsWith("7777")) {
            log.info("Verifying Order(valid)");
            payment.setSuccess(Boolean.TRUE);
            paymentRepository.save(payment);
            return new TaskResult(TaskResult.Result.PASS, "");
        }
        log.info("Verifying Order(not valid)");
        payment.setSuccess(Boolean.FALSE);
        paymentRepository.save(payment);
        return new TaskResult(TaskResult.Result.FAIL, "Invalid credit card number");
    }
}
