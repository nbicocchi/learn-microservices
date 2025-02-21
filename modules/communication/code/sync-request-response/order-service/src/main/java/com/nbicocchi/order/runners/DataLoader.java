package com.nbicocchi.order.runners;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.model.ProductOrder;
import com.nbicocchi.order.persistence.repository.OrderRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
public class DataLoader implements ApplicationRunner {
    private final OrderRepository orderRepository;

    public DataLoader(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Set<Order> orders = Set.of(
                new Order(UUID.randomUUID().toString(), LocalDateTime.now(),
                        Set.of(new ProductOrder("171f5df0-b213-4a40-8ae6-fe82239ab660", 1))),
                new Order(UUID.randomUUID().toString(), LocalDateTime.now(),
                        Set.of(new ProductOrder("f89b6577-3705-414f-8b01-41c091abb5e0", 2),
                                new ProductOrder("b1f4748a-f3cd-4fc3-be58-38316afe1574", 2))));
        orderRepository.saveAll(orders);
    }
}
