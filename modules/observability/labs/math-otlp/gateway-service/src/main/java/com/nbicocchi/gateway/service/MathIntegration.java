package com.nbicocchi.gateway.service;

import com.nbicocchi.gateway.dto.DivisorsWithLatency;
import com.nbicocchi.gateway.dto.MCDWithLatency;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Log4j2
@Component
public class MathIntegration {
    private final RestClient restClient;

    public MathIntegration(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public DivisorsWithLatency getDivisors(Long n, Long times, Long faults) {
        String url = UriComponentsBuilder.fromUriString("http://math-service/divisors")
                .queryParam("n", n)
                .queryParam("times", times)
                .queryParam("faults", faults)
                .toUriString();
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
    }

    public MCDWithLatency getMCD(Long a, Long b, List<Long> aDivisors, List<Long> bDivisors, Long times, Long faults) {
        String url = UriComponentsBuilder.fromUriString("http://math-service/mcd")
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