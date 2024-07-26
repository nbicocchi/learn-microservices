package com.baeldung.name_service_end;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class NameController {

    @Value("${names}")
    private String names;
    private List<String> NAMES;

    @Value("${delayMillis}")
    private int delay;

    private static final Random RND = new Random();

    @GetMapping(value = "/name")
    public Mono<String> name() throws InterruptedException {
        initializeNamesList();
        String randomName = NAMES.get(RND.nextInt(NAMES.size()));
        return Mono.just(randomName)
                .delayElement(Duration.ofMillis(delay));
    }

    private void initializeNamesList() {
        if (NAMES == null) {
            NAMES = Arrays.asList(names.split(",\\s*"));
        }
    }
}
