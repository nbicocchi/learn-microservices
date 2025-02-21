package com.nbicocchi.product.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
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
