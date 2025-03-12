package com.nbicocchi.order.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
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
    private Set<OrderLine> orderLines = new HashSet<>();

    public Order(String uuid, LocalDateTime timestamp, Set<OrderLine> orderLines) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.orderLines = orderLines;
    }
}
