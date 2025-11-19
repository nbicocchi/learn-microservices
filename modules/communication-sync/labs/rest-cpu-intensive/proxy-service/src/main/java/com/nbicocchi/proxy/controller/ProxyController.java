package com.nbicocchi.proxy.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Log4j2
@RestController
public class ProxyController {
    String mathServiceUrl;

    public ProxyController(@Value("${app.math-service.host}") String providerServiceHost, @Value("${app.math-service.port}") int providerServicePort) {
        mathServiceUrl = "http://" + providerServiceHost + ":" + providerServicePort;
    }

    /*
    echo 'GET http://localhost:8080/divisors?number=1234&times=40&email=test@test.com' | vegeta attack -rate=50 -duration=30s | vegeta report
     */
    @GetMapping("/divisors")
    public Map<String, Object> searchPrimes(
            @RequestParam Long number,
            @RequestParam Long times,
            @RequestParam String email) {

        RestClient restClient = RestClient.builder()
                .baseUrl(mathServiceUrl)  // set base URL here
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/divisors")  // only the path, not full URL
                        .queryParam("number", number)
                        .queryParam("times", times)
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}