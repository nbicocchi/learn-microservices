package com.nbicocchi.order.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String uuid;

    private LocalDateTime timestamp;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Set<ProductOrder> products = new HashSet<>();

    public Order(String uuid, LocalDateTime timestamp, Set<ProductOrder> products) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.products = products;
    }
}
