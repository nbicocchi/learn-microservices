package com.nbicocchi.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    @EqualsAndHashCode.Include
    private String uuid;
    private String name;
    private Double weight;
}
