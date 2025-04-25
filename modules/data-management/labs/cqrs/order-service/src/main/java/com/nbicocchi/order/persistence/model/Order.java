package com.nbicocchi.order.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@Entity
public class Order {

    public enum OrderStatus {
        PENDING, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String orderId = UUID.randomUUID().toString();
    private String productIds;
    private String customerId;
    private String creditCardNumber;
    private OrderStatus status = OrderStatus.PENDING;

    public Order(String productIds, String customerId, String creditCardNumber) {
        this.productIds = productIds;
        this.customerId = customerId;
        this.creditCardNumber = creditCardNumber;
    }
}
