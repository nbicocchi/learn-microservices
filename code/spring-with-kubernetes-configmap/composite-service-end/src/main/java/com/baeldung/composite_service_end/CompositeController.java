package com.baeldung.composite_service_end;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class CompositeController {
    private WebClient webClient;
    private String nameServiceUrl;
    private String surnameServiceUrl;

    public CompositeController(WebClient.Builder builder,
                               @Value("${name-service-url}") String nameServiceUrl,
                               @Value("${surname-service-url}") String surnameServiceUrl) {

        webClient = builder.build();
        this.nameServiceUrl = nameServiceUrl;
        this.surnameServiceUrl = surnameServiceUrl;
    }

    @GetMapping("/test")
    public Mono<String> getTest() {
        // Return a static string without calling other services
        return Mono.just("Test OK");
    }

    @GetMapping("/full-name")
    public Mono<String> getCompleteName() throws Exception {

        Mono<String> nameMono = webClient.get().uri(nameServiceUrl).retrieve().bodyToMono(String.class);
        Mono<String> surnameMono = webClient.get().uri(surnameServiceUrl).retrieve().bodyToMono(String.class);

        return Mono.zip(nameMono, surnameMono, (name, surname) -> name + " " + surname);
    }
}
