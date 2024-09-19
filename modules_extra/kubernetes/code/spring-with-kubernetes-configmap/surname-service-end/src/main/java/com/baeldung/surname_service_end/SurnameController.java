package com.baeldung.surname_service_end;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class SurnameController {

    @Value("${surnames}")
    private String surnames;
    private List<String> SURNAMES;

    @Value("${delayMillis}")
    private int delay;
    private static final Random RND = new Random();

    @GetMapping(value = "/surname")
    public Mono<String> surname() throws InterruptedException {
        initializeSurnamesList();
        String randomSurname = SURNAMES.get(RND.nextInt(SURNAMES.size()));
        return Mono.just(randomSurname)
                .delayElement(Duration.ofMillis(delay));
    }

    private void initializeSurnamesList() {
        if (SURNAMES == null) {
            SURNAMES = Arrays.asList(surnames.split(",\\s*"));
        }
    }
}
