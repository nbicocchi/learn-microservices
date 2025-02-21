package com.nbicocchi.consumer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@RestController
public class GreetConsumerController {
    private static final Logger LOG = LoggerFactory.getLogger(GreetConsumerController.class);
    String providerServiceUrl;

    public GreetConsumerController(
            @Value("${app.provider-service.host}") String providerServiceHost,
            @Value("${app.provider-service.port}") int providerServicePort) {
        providerServiceUrl = "http://" + providerServiceHost + ":" + providerServicePort;
    }

    @GetMapping("/greet")
    public String greet() {
        RestClient restClient = RestClient.builder().build();
        try {
            return restClient.get()
                    .uri(providerServiceUrl + "/greet")
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            LOG.error("Error calling REST API");
            return ("Error calling REST API");
        }
    }
}