package com.nbicocchi.product.persistence.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
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
