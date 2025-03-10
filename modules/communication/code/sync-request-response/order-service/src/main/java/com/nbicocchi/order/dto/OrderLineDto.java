package com.nbicocchi.order.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineDto {
    @EqualsAndHashCode.Include
    private String uuid;
    private String name;
    private Double weight;
    private Integer amount;
}
