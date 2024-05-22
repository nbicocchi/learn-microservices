package com.baeldung.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.random.RandomGenerator;

@RestController
public class TimeController {
    private static final Logger LOG = LoggerFactory.getLogger(TimeController.class);
    private static final RandomGenerator RND = RandomGenerator.getDefault();

    @GetMapping(value = "/time")
    public Mono<LocalTime> time() throws InterruptedException {
        LocalTime now = LocalDateTime.now().toLocalTime();
        LOG.info("Returning {}...", now);
        return Mono.just(now)
                .delayElement(Duration.ofMillis(RND.nextInt(2000)));
    }
}
