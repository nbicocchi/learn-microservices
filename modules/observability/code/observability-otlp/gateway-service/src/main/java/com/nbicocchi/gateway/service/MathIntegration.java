package com.nbicocchi.gateway.service;

import com.nbicocchi.gateway.dto.DivisorsWithLatency;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Log4j2
@Component
public class MathIntegration {
    RestClient restClient;

    public MathIntegration(RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder.build();
    }

    @Cacheable(cacheNames = "divisors")
    @Retry(name = "default")
    @CircuitBreaker(name = "default", fallbackMethod = "fallback")
    public DivisorsWithLatency getDivisors(Long n, Long times, Long faults) {
        String url = UriComponentsBuilder.fromHttpUrl("http://MATH-SERVICE/divisors")
                .queryParam("n", n)
                .queryParam("times", times)
                .queryParam("faults", faults)
                .toUriString();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public DivisorsWithLatency fallback(Long n, Long times, Long faults, CallNotPermittedException ex) {
        return new DivisorsWithLatency(0L, List.of(0L), 0L);
    }
}
