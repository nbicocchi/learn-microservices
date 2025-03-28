package com.nbicocchi.composite.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DateTimeIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeIntegration.class);
    private static final String DATETIME_SERVICE_URL = "http://DATETIME-SERVICE";
    RestClient restClient;

    public DateTimeIntegration(RestClient.Builder builder) {
        restClient = builder.build();
    }

    @Retry(name = "time")
    //@TimeLimiter(name = "time")
    //@CircuitBreaker(name = "time", fallbackMethod = "getTimeFallbackValue")
    public LocalTime getTime(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(TIME_SERVICE_URL + "?delay={delay}&faultPercent={faultPercent}").build(delay, faultPercent);

        LOG.info("Calling time API on URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(LocalTime.class);
    }

    public LocalTime getTimeFallbackValue(int delay, int faultPercent, CallNotPermittedException e) {
        return LocalTime.of(11, 11, 11);
    }

    public LocalDate getDate(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(DATE_SERVICE_URL + "?delay={delay}&faultPercent={faultPercent}").build(delay, faultPercent);

        LOG.info("Calling date API on URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(LocalDate.class);
    }

    @Bulkhead(name = "time")
    public LocalTime getTimeWithBulkhead(int delay, int faultPercent) {
        return getTime(delay, faultPercent);
    }

    @Bulkhead(name = "date")
    public LocalDate getDateWithBulkhead(int delay, int faultPercent) {
        return getDate(delay, faultPercent);
    }

}
