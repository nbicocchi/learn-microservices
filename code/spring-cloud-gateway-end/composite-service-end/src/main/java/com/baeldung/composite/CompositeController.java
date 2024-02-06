package com.baeldung.composite;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class CompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeController.class);

    WebClient webClient;

    public CompositeController(WebClient.Builder builder) {
        webClient = builder.build();
    }

    @GetMapping(value = "/datetime")
    public Mono<LocalDateTime> dateTime() throws InterruptedException {
        String urlTime = "http://TIME-SERVICE/time";
        String urlDate = "http://DATE-SERVICE/date";

        LOG.info("Calling time API on URL: {}", urlTime);
        Mono<LocalTime> localTimeMono = webClient.get().uri(urlTime).retrieve().bodyToMono(LocalTime.class);

        LOG.info("Calling time API on URL: {}", urlDate);
        Mono<LocalDate> localDateMono = webClient.get().uri(urlDate).retrieve().bodyToMono(LocalDate.class);

        return Mono.zip(localDateMono, localTimeMono,
                (localDate, localTime) -> LocalDateTime.of(localDate, localTime));
    }
}
