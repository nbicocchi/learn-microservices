package com.example.check;


import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import io.opentelemetry.api.trace.Tracer;

@RestController
public class CheckController {

    private static final Logger logger = LoggerFactory.getLogger(CheckController.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private RestTemplate restTemplate; // Iniezione tramite Spring

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/health")
    public String healthCheck() {
        // Creazione dello span
        Span span = tracer.spanBuilder("healthCheck").startSpan();
        try (Scope scope = span.makeCurrent()) {
            String response;
            try {
                // Usa DiscoveryClient per ottenere l'URL del servizio 'TIMESERVICE' da Eureka
                String serviceUrl = discoveryClient.getInstances("TIMESERVICE")
                        .stream()
                        .findFirst()
                        .map(serviceInstance -> serviceInstance.getUri().toString())
                        .orElseThrow(() -> new RuntimeException("TIMESERVICE not found"));

                // Effettua la richiesta all'endpoint /time del servizio TIMESERVICE
                response = restTemplate.getForObject(serviceUrl + "/time", String.class);

                logger.info("Service is up: {}", response);
                return "Service is up: " + response;
            } catch (Exception e) {
                span.recordException(e);
                logger.error("Service is down: {}", e.getMessage());
                return "Service is down";
            }
        } finally {
            span.end();
        }
    }
}
