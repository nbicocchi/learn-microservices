package com.nbicocchi.lab.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class GreetingConsumerController {
    String productServiceUrl;

    public GreetingConsumerController(
            @Value("${app.greet-service.host}") String productServiceHost,
            @Value("${app.greet-service.port}") int productServicePort) {
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/greet";
    }

    @GetMapping("/consume-greet")
    public String consumeGreet() {
        RestClient restClient = RestClient.builder().build();
        return restClient.get()
                .uri(productServiceUrl)
                .retrieve()
                .body(String.class);
    }
}