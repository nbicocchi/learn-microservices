package com.nbicocchi.composite.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class DummyIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(DummyIntegration.class);
    private static final String OK_SERVICE_URL = "http://dummy:8081/ok";
    private static final String NOT_OK_SERVICE_URL = "http://dummy:8081/notOk";
    private static final String MAY_FAIL_SERVICE_URL = "http://dummy:8081/mayFail";
    RestClient restClient;

    public DummyIntegration(RestClient.Builder builder) {
        restClient = builder.build();
    }

    @CircuitBreaker(name = "ok", fallbackMethod = "getOkFallbackValue")
    public String getOk() {
        URI url = UriComponentsBuilder.fromUriString(OK_SERVICE_URL).build().toUri();

        LOG.info("Calling API on URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

    @CircuitBreaker(name = "notok", fallbackMethod = "getNotOkFallbackValue")
    public String getNotOk() {
        URI url = UriComponentsBuilder.fromUriString(NOT_OK_SERVICE_URL).build().toUri();

        LOG.info("Calling API on URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

    @Retry(name = "mayFail")
    public String mayFail() {
        URI url = UriComponentsBuilder.fromUriString(MAY_FAIL_SERVICE_URL).build().toUri();

        LOG.info("Calling API on URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

    public String getOkFallbackValue(CallNotPermittedException e) {
        return "Ok-fallback";
    }

    public String getNotOkFallbackValue(CallNotPermittedException e) {
        return "NotOk-fallback";
    }
}
