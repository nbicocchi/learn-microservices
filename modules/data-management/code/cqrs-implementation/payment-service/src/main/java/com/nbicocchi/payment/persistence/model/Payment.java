package com.nbicocchi.payment.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String orderId;
    private String creditCardNumber;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Boolean success = Boolean.FALSE;

    public Payment(String orderId, String creditCardNumber) {
        this.orderId = orderId;
        this.creditCardNumber = creditCardNumber;
    }
}
