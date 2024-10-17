package com.nbicocchi.consumer_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


@RestController
public class GreetConsumerControllerImpl implements GreetConsumerController {
    String providerServiceUrl;
    private static final Logger LOG = LoggerFactory.getLogger(GreetConsumerControllerImpl.class);

    public GreetConsumerControllerImpl(
            @Value("${app.provider-service.host}") String providerServiceHost,
            @Value("${app.provider-service.port}") int providerServicePort) {
        providerServiceUrl = "http://" + providerServiceHost + ":" + providerServicePort + "/greet";
    }

    @GetMapping("/greet")
    public String greet() {
        RestClient restClient = RestClient.builder().build();
        try {
            return restClient.get()
                    .uri(providerServiceUrl)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            LOG.error("Error calling REST API");
            return ("Error calling REST API");
        }
    }
}