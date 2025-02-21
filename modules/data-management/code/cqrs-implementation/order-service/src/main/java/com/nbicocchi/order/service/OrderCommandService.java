package com.nbicocchi.order.service;

import com.nbicocchi.order.events.OrderCreatedEvent;
import com.nbicocchi.order.events.OrderDeletedEvent;
import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.model.Order.OrderStatus;
import com.nbicocchi.order.persistence.repository.command.OrderCommandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class OrderCommandService {
    private final OrderCommandRepository orderCommandRepository;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public Order createOrder(String productId, String customerIds, String creditCardNumber) {
        Order order = new Order(productId, customerIds, creditCardNumber);

        log.info("Trying to contact payment with order ID " + order.getOrderId() + " and credit card number " + creditCardNumber);
        String paymentUrl = "http://payment-service:9001/payment?orderId=" + order.getOrderId()
                + "&creditCardNumber=" + creditCardNumber;
        Boolean paymentSuccess;
        try {
            paymentSuccess = restTemplate.postForObject(paymentUrl, null, Boolean.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Payment service not available: " + e.getMessage());
        }

        if(!Boolean.TRUE.equals(paymentSuccess)) {
            order.setStatus(OrderStatus.REJECTED);
            orderCommandRepository.save(order);
            eventPublisher.publishEvent(new OrderCreatedEvent(order));
            throw new IllegalArgumentException("Payment failed.");
        }

        log.info("Trying to contact inventory with product ID " + productId);
        String inventoryUrl = "http://inventory-service:9002/inventory/" + productId;
        Boolean inventoryValid;
        try {
            inventoryValid = restTemplate.exchange(inventoryUrl, HttpMethod.PUT, null, Boolean.class).getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Inventory service not available: " + e.getMessage());
        }

        if(!Boolean.TRUE.equals(inventoryValid)) {
            order.setStatus(OrderStatus.REJECTED);
            orderCommandRepository.save(order);
            eventPublisher.publishEvent(new OrderCreatedEvent(order));
            throw new IllegalArgumentException("Inventory not sufficient for order.");
        }

        order.setStatus(OrderStatus.APPROVED);
        orderCommandRepository.save(order);
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        return order;
    }

    public Order deleteOrder(String orderId) {
        orderCommandRepository.findByOrderId(orderId).ifPresent(orderCommandRepository::delete);
        eventPublisher.publishEvent(new OrderDeletedEvent(orderId));
        return null;
    }
}