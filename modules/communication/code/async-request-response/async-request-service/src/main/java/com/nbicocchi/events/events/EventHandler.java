package com.nbicocchi.events.events;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Log4j2
@Configuration
public class EventHandler {
    @Bean
    public Consumer<Event<String, Double>> mathResponse() {
        return event -> log.info("[RESPONSE] -> {}", event);
    }
}
