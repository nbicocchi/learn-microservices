package com.nbicocchi.composite.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Log4j2
@Component
public class DateTimeIntegration {
    private static final String TIME_SERVICE_URL = "http://TIME-SERVICE";
    private static final String DATE_SERVICE_URL = "http://DATE-SERVICE";
    RestClient restClient;

    public DateTimeIntegration(RestClient.Builder builder) {
        restClient = builder.build();
    }

    //@Retry(name = "time")
    //@TimeLimiter(name = "time")
    @CircuitBreaker(name = "time", fallbackMethod = "getTimeFallbackValue")
    public LocalTime getTime(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(TIME_SERVICE_URL + "/time" + "?delay={delay}&faultPercent={faultPercent}").build(delay, faultPercent);

        log.info("Calling time API on URL: {}", url);
        Map<String, LocalTime> map = restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return map.get("time");
    }

    public LocalDate getDate(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(DATE_SERVICE_URL + "/date" + "?delay={delay}&faultPercent={faultPercent}").build(delay, faultPercent);

        log.info("Calling date API on URL: {}", url);
        Map<String, LocalDate> map = restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return map.get("date");
    }

    @Bulkhead(name = "time")
    public LocalTime getTimeWithBulkhead(int delay, int faultPercent) {
        return getTime(delay, faultPercent);
    }

    @Bulkhead(name = "date")
    public LocalDate getDateWithBulkhead(int delay, int faultPercent) {
        return getDate(delay, faultPercent);
    }

    public LocalTime getTimeFallbackValue(int delay, int faultPercent, CallNotPermittedException e) {
        return LocalTime.of(LocalTime.now().getHour(), 0, 0);
    }

    public LocalDate getDateFallbackValue(int delay, int faultPercent, CallNotPermittedException e) {
        return LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 0);
    }
}
