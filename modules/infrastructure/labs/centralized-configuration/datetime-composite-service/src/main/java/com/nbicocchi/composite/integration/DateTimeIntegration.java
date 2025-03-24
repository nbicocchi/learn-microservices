package com.nbicocchi.composite.integration;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Log4j2
@Service
public class DateTimeIntegration {
    RestClient restClient;

    public DateTimeIntegration(RestClient.Builder clientBuilder) {
        restClient = clientBuilder.build();
    }

    public LocalDate getDate() {
        String url = "http://DATETIME-SERVICE/date";
        log.info("Calling time API on URL: {}", url);
        Map<String, LocalDate> map = restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return map.get("LocalDate");
    }

    public LocalTime getTime() {
        String url = "http://DATETIME-SERVICE/time";
        log.info("Calling time API on URL: {}", url);
        Map<String, LocalTime> map = restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return map.get("LocalTime");
    }
}
