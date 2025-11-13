package com.nbicocchi.gateway.service;

import com.nbicocchi.gateway.dto.DivisorsWithLatency;
import com.nbicocchi.gateway.dto.MCDWithLatency;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Log4j2
@Component
public class MathIntegration {
    private final RestClient restClient;

    public MathIntegration(RestClient.Builder restClientBuilder, MeterRegistry meterRegistry) {
        this.restClient = restClientBuilder.build();
    }

    @Retry(name = "divisors")
    //@CircuitBreaker(name = "divisors")
    //@Bulkhead(name = "divisors")
    public DivisorsWithLatency getDivisors(Long n, Long times, Long faults) {
        String url = UriComponentsBuilder.fromHttpUrl("http://MATH-SERVICE/divisors")
                .queryParam("n", n)
                .queryParam("times", times)
                .queryParam("faults", faults)
                .toUriString();
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
    }

    @Retry(name = "mcd")
    //@CircuitBreaker(name = "mcd")
    //@Bulkhead(name = "mcd")
    public MCDWithLatency getMCD(Long a, Long b, List<Long> aDivisors, List<Long> bDivisors, Long times, Long faults) {
        String url = UriComponentsBuilder.fromHttpUrl("http://MATH-SERVICE/mcd")
                .queryParam("a", a)
                .queryParam("b", b)
                .queryParam("aDivisors", aDivisors)
                .queryParam("bDivisors", bDivisors)
                .queryParam("times", times)
                .queryParam("faults", faults)
                .toUriString();
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
    }
}