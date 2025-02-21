package com.nbicocchi.order.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false, unique = false)
    private Integer amount;

    public ProductOrder(String uuid, Integer amount) {
        this.uuid = uuid;
        this.amount = amount;
    }
}