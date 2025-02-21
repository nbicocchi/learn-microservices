package com.nbicocchi.product.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @EqualsAndHashCode.Include
    private String uuid;
    private String name;
    private Double weight;

    public Product(String uuid, String name, Double weight) {
        this.uuid = uuid;
        this.name = name;
        this.weight = weight;
    }
}
