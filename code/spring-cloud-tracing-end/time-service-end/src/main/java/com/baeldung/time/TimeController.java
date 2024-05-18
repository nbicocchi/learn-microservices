package com.baeldung.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@RestController
public class TimeController {
    private static final Logger LOG = LoggerFactory.getLogger(TimeController.class);
    private static final Random randomNumberGenerator = new Random();

    @GetMapping(value = "/time")
    public Mono<LocalTime> time(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        LOG.info("Returning time...");
        LocalTime now = LocalDateTime.now().toLocalTime();
        LOG.info("Returning {}...", now);
        return Mono.just(now)
                .delayElement(Duration.ofSeconds(delay))
                .map(e -> throwErrorIfBadLuck(e, faultPercent));
    }

    private LocalTime throwErrorIfBadLuck(LocalTime entity, int faultPercent) {
        if (faultPercent == 0) {
            return entity;
        }

        int randomThreshold = randomNumberGenerator.nextInt(0, 100);
        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.info("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }
        return entity;
    }
}
