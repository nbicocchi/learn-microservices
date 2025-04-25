package com.nbicocchi.order_read.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@Entity
public class OrderRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerId;
    private Integer m;
    private Integer y;
    private Integer n;

    public OrderRead(String customerId, Integer m, Integer y, Integer n) {
        this.customerId = customerId;
        this.m = m;
        this.y = y;
        this.n = n;
    }


}
