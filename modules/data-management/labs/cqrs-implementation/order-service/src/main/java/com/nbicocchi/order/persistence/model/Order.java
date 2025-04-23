package com.nbicocchi.order.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@Table(name = "\"order\"")
@Entity
public class Order {

    public enum OrderStatus {
        APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String orderId = UUID.randomUUID().toString();
    private String productId;
    private String customerId;
    private String creditCardNumber;
    private OrderStatus status;

    public Order(String productId, String customerId, String creditCardNumber) {
        this.productId = productId;
        this.customerId = customerId;
        this.creditCardNumber = creditCardNumber;
    }
}
