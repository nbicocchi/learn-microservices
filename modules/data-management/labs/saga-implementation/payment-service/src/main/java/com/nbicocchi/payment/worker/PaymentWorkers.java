package com.nbicocchi.payment.worker;

import com.nbicocchi.payment.pojos.Order;
import com.nbicocchi.payment.persistence.model.Payment;
import com.nbicocchi.payment.persistence.repository.PaymentRepository;
import com.nbicocchi.payment.pojos.TaskResult;
import com.nbicocchi.payment.service.CardValidatorService;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class PaymentWorkers {
    PaymentRepository paymentRepository;
    CardValidatorService cardValidatorService;

    @WorkerTask(value = "payment-check", threadCount = 1, pollingInterval = 200)
    public TaskResult paymentCheck(Order order) {
        log.info("Verifying {}...", order);
        Payment payment = new Payment(order.getOrderId(), order.getCreditCardNumber());
        if (cardValidatorService.validateCard(order.getCreditCardNumber())) {
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
