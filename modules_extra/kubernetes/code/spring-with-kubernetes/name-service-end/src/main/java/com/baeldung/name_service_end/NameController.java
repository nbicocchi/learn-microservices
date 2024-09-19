package com.baeldung.name_service_end;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class NameController {

    private static final List<String> NAMES = Arrays.asList(
            "Luca", "Marco", "Giulia", "Francesca", "Matteo",
            "Elena", "Davide", "Silvia", "Alessandro", "Chiara"
    );
    private static final Random RND = new Random();

    @GetMapping(value = "/name")
    public Mono<String> name() throws InterruptedException {
        String randomName = NAMES.get(RND.nextInt(NAMES.size()));
        return Mono.just(randomName)
                .delayElement(Duration.ofMillis(RND.nextInt(1000)));
    }
}
