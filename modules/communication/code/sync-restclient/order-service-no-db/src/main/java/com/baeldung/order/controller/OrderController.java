package com.baeldung.order.controller;

import com.baeldung.order.persistence.model.Order;
import com.baeldung.order.persistence.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    RestClient restClient;
    String productServiceUrl;

    public OrderController(
        @Value("${app.product-service.host}") String productServiceHost,
        @Value("${app.product-service.port}") int productServicePort) {
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/products";
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        RestClient restClient = RestClient.builder().build();
        List<Product> products = restClient.get()
                .uri(productServiceUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return new Order("order-x", LocalDate.now(), products);
    }
}