package com.nbicocchi.gateway.service;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ServiceRefresher {
    private final ReactiveDiscoveryClient discoveryClient;
    private final WebClient webClient;

    public ServiceRefresher(ReactiveDiscoveryClient discoveryClient, WebClient.Builder webClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.webClient = webClientBuilder.build();
    }

    public Mono<Void> refreshAllServices() {
        return discoveryClient.getServices() // Fetch all registered services reactively
                .flatMap(discoveryClient::getInstances) // Get instances for each service
                .flatMap(this::refreshInstance) // Call /actuator/refresh for each instance
                .then(); // Return Mono<Void>
    }

    private Mono<Void> refreshInstance(ServiceInstance instance) {
        String url = instance.getUri().toString() + "/actuator/refresh";
        System.out.println("Refreshing: " + url);

        return webClient.post()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> System.out.println("✅ Refreshed: " + url))
                .doOnError(e -> System.err.println("❌ Failed to refresh " + url + " - " + e.getMessage()))
                .onErrorResume(e -> Mono.empty()); // Avoid breaking the chain if one request fails
    }
}
