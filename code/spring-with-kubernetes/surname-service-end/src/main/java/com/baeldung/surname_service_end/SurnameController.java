package com.baeldung.surname_service_end;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class SurnameController {
    private static final List<String> SURNAMES = Arrays.asList(
            "Rossi", "Bianchi", "Russo", "Ferrari", "Esposito",
            "Bianchi", "Romano", "Colombo", "Conti", "Moretti"
    );
    private static final Random RND = new Random();

    @GetMapping(value = "/surname")
    public Mono<String> surname() throws InterruptedException {
        String randomSurname = SURNAMES.get(RND.nextInt(SURNAMES.size()));
        return Mono.just(randomSurname)
                .delayElement(Duration.ofMillis(RND.nextInt(1000)));
    }
}
