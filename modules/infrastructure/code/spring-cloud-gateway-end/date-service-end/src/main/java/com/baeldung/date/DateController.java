package com.baeldung.date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.random.RandomGenerator;

@RestController
public class DateController {
    private static final RandomGenerator RND = RandomGenerator.getDefault();

    @GetMapping(value = "/date")
    public Mono<LocalDate> time() throws InterruptedException {
        return Mono.just(LocalDateTime.now().toLocalDate())
                .delayElement(Duration.ofMillis(RND.nextInt(1000)));
    }
}
