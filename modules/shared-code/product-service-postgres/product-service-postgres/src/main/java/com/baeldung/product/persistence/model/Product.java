package com.baeldung.product.persistence.model;

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
    private String name;
    private Double weight;

    public Product(String name, Double weight) {
        this(null, name, weight);
    }

    public Product(Product product) {
        this(product.getId(), product.getName(), product.getWeight());
    }
}
