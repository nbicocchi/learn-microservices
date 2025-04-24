package com.nbicocchi.payment.pojos;

import lombok.*;

@Data
public class Order {

    public enum OrderStatus {
        PENDING, APPROVED, REJECTED
    }

    @EqualsAndHashCode.Include
    private String orderId;
    private String productIds;
    private String customerId;
    private String creditCardNumber;
    private OrderStatus status;
}

