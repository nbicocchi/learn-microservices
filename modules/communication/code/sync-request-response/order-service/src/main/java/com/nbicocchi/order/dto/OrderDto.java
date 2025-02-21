package com.nbicocchi.order.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    @EqualsAndHashCode.Include
    private String uuid;
    private LocalDateTime timestamp;
    private Set<ProductDto> products = new HashSet<>();
}
