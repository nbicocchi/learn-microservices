package com.nbicocchi.composite.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Component
public class DateTimeIntegration {
    RestClient restClient;

    public DateTimeIntegration(RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder.build();
    }

    @Retry(name = "time")
    @CircuitBreaker(name = "time", fallbackMethod = "getTimeFallbackValue")
    public LocalTime getTime() {
        Map<String, LocalTime> map = restClient.get()
                .uri("http://DATETIME-SERVICE/time")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return Objects.requireNonNull(map).get("time");
    }

    public LocalDate getDate() {
        Map<String, LocalDate> map = restClient.get()
                .uri("http://DATETIME-SERVICE/date")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return Objects.requireNonNull(map).get("date");
    }

    @Bulkhead(name = "time")
    public LocalTime getTimeWithBulkhead() {
        return getTime();
    }

    @Bulkhead(name = "date")
    public LocalDate getDateWithBulkhead() {
        return getDate();
    }

    public LocalTime getTimeFallbackValue(int delay, int faultPercent, CallNotPermittedException ex) {
        return LocalTime.of(LocalTime.now().getHour(), 0, 0);
    }

    public LocalDate getDateFallbackValue(int delay, int faultPercent, CallNotPermittedException ex) {
        return LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 0);
    }
}
