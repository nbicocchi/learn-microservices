package com.baeldung.time;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.random.RandomGenerator;

@RestController
public class TimeController {
    private static final RandomGenerator RND = RandomGenerator.getDefault();

    @GetMapping(value = "/time")
    public Mono<LocalTime> time() throws InterruptedException {
        return Mono.just(LocalDateTime.now().toLocalTime())
                .delayElement(Duration.ofMillis(RND.nextInt(2000)));
    }
}
