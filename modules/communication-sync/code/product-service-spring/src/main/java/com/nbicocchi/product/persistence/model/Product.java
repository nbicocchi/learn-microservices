package com.nbicocchi.product.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @EqualsAndHashCode.Include
    private String uuid;

    @NonNull private String name;
    @NonNull private Double weight;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    // Constructor for creating a product without UUID (it will be auto-generated)
    public Product(String name, Double weight) {
        this.name = name;
        this.weight = weight;
    }
}
