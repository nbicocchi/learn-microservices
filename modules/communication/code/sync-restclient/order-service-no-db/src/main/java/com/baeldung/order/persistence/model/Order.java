package com.baeldung.order.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private Long id;
    private String name;
    private LocalDate date;
    private Collection<Product> products;

    public Order(String name, LocalDate date, Collection<Product> products) {
        this(null, name, date, new ArrayList<>(products));
    }

    public Order(Order order) {
        this(order.getId(), order.getName(), order.getDate(), new ArrayList<>(order.getProducts()));
    }
}
