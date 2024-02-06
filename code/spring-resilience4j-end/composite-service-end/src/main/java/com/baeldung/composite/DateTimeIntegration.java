package com.baeldung.composite;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalTime;

@Component
public class DateTimeIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeIntegration.class);
    private static final String TIME_SERVICE_URL = "http://TIME-SERVICE/time";

    WebClient webClient;

    public DateTimeIntegration(WebClient.Builder builder) {
        webClient = builder.build();
    }

    @Retry(name = "time")
    @TimeLimiter(name = "time")
    @CircuitBreaker(name = "time", fallbackMethod = "getTimeFallbackValue")
    public Mono<LocalTime> getTime(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(TIME_SERVICE_URL + "?delay={delay}&faultPercent={faultPercent}").build(delay, faultPercent);

        LOG.info("Getting time on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(LocalTime.class)
                .doOnError(ex -> handleException(ex));
    }

    private Mono<LocalTime> getTimeFallbackValue(int delay, int faultPercent, CallNotPermittedException ex) {
        LOG.warn("Creating a fail-fast fallback date, delay = {}, faultPercent = {} and exception = {} ", delay, faultPercent, ex.toString());
        return Mono.just(LocalTime.of(00, 00, 00));
    }

    private void handleException(Throwable ex) {
        LOG.warn("Attempt --> " + ex.getMessage());
    }
}
