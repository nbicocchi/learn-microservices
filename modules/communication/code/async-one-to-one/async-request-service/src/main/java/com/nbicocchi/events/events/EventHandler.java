package com.nbicocchi.events.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);

    @Bean
    public Consumer<Event<String, Integer>> mathResponse() {
        return event -> LOG.info("[RESPONSE] -> {}", event);
    }
}
