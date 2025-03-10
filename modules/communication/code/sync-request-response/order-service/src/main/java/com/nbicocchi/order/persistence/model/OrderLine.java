package com.nbicocchi.order.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false, unique = false)
    private Integer amount;

    public OrderLine(String uuid, Integer amount) {
        this.uuid = uuid;
        this.amount = amount;
    }
}