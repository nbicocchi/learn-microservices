package com.nbicocchi.proxy.controller;

import com.nbicocchi.proxy.dto.ProxyRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Log4j2
@RestController
public class ProxyController {
    String mathServiceUrl;

    public ProxyController(
            @Value("${app.math-service.host}") String providerServiceHost,
            @Value("${app.math-service.port}") int providerServicePort) {
        mathServiceUrl = "http://" + providerServiceHost + ":" + providerServicePort;
    }

    /**
     * curl -X POST "http://localhost:8080/primes" -H "Content-Type: application/json" -d '{ "lowerBound": 10, "upperBound": 1000, "email": "example@example.com" }'
     */
    @PostMapping("/primes")
    public Iterable<Long> primes(@RequestBody ProxyRequest request) {
        log.info("endpoint /primes() invoked");
        RestClient restClient = RestClient.builder().build();
        return restClient.post()
                .uri(mathServiceUrl + "/primes")
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}