package com.nbicocchi.composite.controller;

import com.nbicocchi.composite.model.LocalDateTimeWithTimestamp;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class CompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeController.class);
    private final RestClient.Builder restClientBuilder;

    public CompositeController(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @GetMapping(value = "/datetime")
    public LocalDateTimeWithTimestamp dateTime() {
        RestClient restClient = restClientBuilder.build();
        String urlTime = "http://DATETIME-SERVICE/time";
        String urlDate = "http://DATETIME-SERVICE/date";

        LOG.info("Calling time API on URL: {}", urlTime);
        LocalTime localTime = restClient.get()
                .uri(urlTime)
                .retrieve()
                .body(LocalTime.class);

        LOG.info("Calling time API on URL: {}", urlDate);
        LocalDate localDate = restClient.get()
                .uri(urlDate)
                .retrieve()
                .body(LocalDate.class);

        return new LocalDateTimeWithTimestamp(localDate, localTime, LocalDateTime.now());
    }
}
