package com.baeldung.composite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CompositeController {

    private final CompositeService compositeService;

    @Autowired
    public CompositeController(CompositeService dateTimeCompositeService) {
        this.compositeService = dateTimeCompositeService;
    }

    @GetMapping("/datetime")
    public Mono<String> getDateTime() {

        return compositeService.getDateTime();
    }

    @Value("${configuration}")
    private String configuration;

    @GetMapping("/configuration")
    public String getMessage() {

        return this.configuration;
    }
}
