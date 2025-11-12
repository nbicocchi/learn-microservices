package com.nbicocchi.gateway.service;

import com.nbicocchi.gateway.dto.DivisorsWithLatency;
import com.nbicocchi.gateway.dto.MCDWithLatency;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
/**
 * Componente responsabile dell'integrazione con servizi esterni per operazioni matematiche.
 * Utilizza RestClient per effettuare chiamate HTTP e Resilience4j per la resilienza.
 */
@Log4j2
@Component
public class MathIntegration {

    private final RestClient restClient;
    private final Counter mathServiceErrors;
    private final Counter mcdServiceErrors;


    /**
     * Costruttore della classe MathIntegration.
     *
     * @param restClientBuilder il builder per creare un'istanza di RestClient
     */

    public MathIntegration(RestClient.Builder restClientBuilder, MeterRegistry meterRegistry) {
        this.restClient = restClientBuilder.build();
        this.mathServiceErrors = meterRegistry.counter("errors.mathservice.down");
        this.mcdServiceErrors = meterRegistry.counter("errors.mcdservice.down");
    }




    /**
     * Recupera i divisori di un numero intero chiamando il servizio MATH-SERVICE.
     * Utilizza meccanismi di resilienza tramite Retry e CircuitBreaker.
     *
     * @param n      numero di cui calcolare i divisori
     * @param times  numero di ritardi da simulare (per test)
     * @param faults numero di errori da simulare (per test)
     * @return un oggetto {@link DivisorsWithLatency} contenente i divisori e la latenza
     */
    public DivisorsWithLatency getDivisors(Long n, Long times, Long faults) {
        String url = UriComponentsBuilder.fromHttpUrl("http://MATH-SERVICE/divisors")
                .queryParam("n", n)
                .queryParam("times", times)
                .queryParam("faults", faults)
                .toUriString();
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            mathServiceErrors.increment();
            throw e;
        }
    }
     /**
     * Recupera il massimo comune divisore tra due numeri, chiamando il servizio MCD-SERVICE.
     * Accetta anche i divisori pre-calcolati come input.
     *
     * @param a         primo numero
     * @param b         secondo numero
     * @param aDivisors lista di divisori del primo numero
     * @param bDivisors lista di divisori del secondo numero
     * @param times     numero di ritardi da simulare
     * @param faults    numero di errori da simulare
     * @return un oggetto {@link MCDWithLatency} con l'MCD calcolato e la latenza
     */

    public MCDWithLatency getMCD(Long a, Long b, List<Long> aDivisors, List<Long> bDivisors, Long times, Long faults) {
        String url = UriComponentsBuilder.fromHttpUrl("http://MCD-SERVICE/mcd")
                .queryParam("a", a)
                .queryParam("b", b)
                .queryParam("aDivisors", aDivisors)
                .queryParam("bDivisors", bDivisors)
                .queryParam("times", times)
                .queryParam("faults", faults)
                .toUriString();
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            mcdServiceErrors.increment();
            throw e;
        }
    }
}