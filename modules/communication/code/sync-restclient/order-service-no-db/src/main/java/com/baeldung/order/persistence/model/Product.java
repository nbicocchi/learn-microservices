package com.baeldung.order.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
