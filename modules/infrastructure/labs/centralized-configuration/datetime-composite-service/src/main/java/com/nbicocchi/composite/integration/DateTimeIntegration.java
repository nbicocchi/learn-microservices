package com.nbicocchi.composite.integration;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Log4j2
@Service
public class DateTimeIntegration {
    private final RestClient.Builder restClientBuilder;

    public DateTimeIntegration(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public LocalDate getDate() {
        RestClient restClient = restClientBuilder.build();
        String url = "http://datetime-service/date";
        log.info("Calling time API on URL: {}", url);
        Map<String, LocalDate> map = restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return map.get("LocalDate");
    }

    public LocalTime getTime() {
        RestClient restClient = restClientBuilder.build();
        String url = "http://datetime-service/time";
        log.info("Calling time API on URL: {}", url);
        Map<String, LocalTime> map = restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return map.get("LocalTime");
    }
}
