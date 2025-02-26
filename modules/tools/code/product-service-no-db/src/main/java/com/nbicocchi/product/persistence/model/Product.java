package com.nbicocchi.product.persistence.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class Product {
    private Long id;
    @NonNull @EqualsAndHashCode.Include private String uuid;
    @NonNull private String name;
    @NonNull private Double weight;
}
