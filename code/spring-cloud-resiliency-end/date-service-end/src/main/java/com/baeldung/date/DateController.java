package com.baeldung.date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.random.RandomGenerator;

@RestController
public class DateController {
    private static final RandomGenerator RND = RandomGenerator.getDefault();

    @GetMapping(value = "/date")
    public Mono<LocalDate> date(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay
    ) {
        return Mono.just(LocalDateTime.now().toLocalDate())
                .delayElement(Duration.ofSeconds(delay + RND.nextInt(1)));
    }
}
