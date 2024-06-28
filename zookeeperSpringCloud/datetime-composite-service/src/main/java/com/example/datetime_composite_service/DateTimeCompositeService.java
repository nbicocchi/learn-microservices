package com.example.datetime_composite_service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DateTimeCompositeService {

    private final WebClient.Builder webClientBuilder;
    private final String timeServiceUrl;
    private final String dateServiceUrl;

    public DateTimeCompositeService(
            WebClient.Builder webClientBuilder,
            @Value("${time.service.url}") String timeServiceUrl,
            @Value("${date.service.url}") String dateServiceUrl) {
        this.webClientBuilder = webClientBuilder;
        this.timeServiceUrl = timeServiceUrl;
        this.dateServiceUrl = dateServiceUrl;
    }

    public Mono<String> getDateTime() {
        Mono<String> timeMono = webClientBuilder.build().get()
                .uri(timeServiceUrl)
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> dateMono = webClientBuilder.build().get()
                .uri(dateServiceUrl)
                .retrieve()
                .bodyToMono(String.class);

        return Mono.zip(timeMono, dateMono, (time, date) -> "Date: " + date + ", Time: " + time);
    }
}