package com.nbicocchi.order;

import com.nbicocchi.order.persistence.model.Order;
import com.nbicocchi.order.persistence.model.OrderLine;
import com.nbicocchi.order.persistence.repository.OrderRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class App implements ApplicationRunner {
    OrderRepository orderRepository;

    public App(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        Set<Order> orders = Set.of(
                new Order(UUID.randomUUID().toString(), LocalDateTime.now(),
                        Set.of(new OrderLine("171f5df0-b213-4a40-8ae6-fe82239ab660", 1))),
                new Order(UUID.randomUUID().toString(), LocalDateTime.now(),
                        Set.of(new OrderLine("f89b6577-3705-414f-8b01-41c091abb5e0", 2),
                                new OrderLine("b1f4748a-f3cd-4fc3-be58-38316afe1574", 2))));
        orderRepository.saveAll(orders);
    }
}
