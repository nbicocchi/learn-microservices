package com.baeldung.composite_service_end;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class CompositeController {
    private WebClient webClient;

    public CompositeController(WebClient.Builder builder) {

        webClient = builder.build();
    }

    @GetMapping("/test")
    public Mono<String> getTest() {
        // Return a static string without calling other services
        return Mono.just("Test OK");
    }

    @GetMapping("/full-name")
    public Mono<String> getCompleteName() throws Exception {
        String urlName = "http://name-service-end/name";
        String urlSurname = "http://surname-service-end/surname";

        Mono<String> nameMono = webClient.get().uri(urlName).retrieve().bodyToMono(String.class);
        Mono<String> surnameMono = webClient.get().uri(urlSurname).retrieve().bodyToMono(String.class);

        return Mono.zip(nameMono, surnameMono, (name, surname) -> name + " " + surname);
    }
}
