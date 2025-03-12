package com.nbicocchi.consumer.controller;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class GreetConsumerController {
    String providerServiceUrl;

    public GreetConsumerController(
            @Value("${app.provider-service.host}") String providerServiceHost,
            @Value("${app.provider-service.port}") int providerServicePort) {
        providerServiceUrl = "http://" + providerServiceHost + ":" + providerServicePort;
    }

    /**
     * Use with:
     * seq 1 30 | xargs -n1 -P3 -I{} curl "http://localhost:8080/greet" | jq
     */
    @GetMapping("/greet")
    public Map<String, String> greet() {
        log.info("endpoint /greet() invoked");
        RestClient restClient = RestClient.builder().build();
        return restClient.get()
                    .uri(providerServiceUrl + "/greet")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
    }
}