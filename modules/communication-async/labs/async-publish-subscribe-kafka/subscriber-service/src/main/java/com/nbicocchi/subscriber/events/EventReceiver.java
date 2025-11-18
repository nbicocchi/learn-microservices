package com.nbicocchi.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
@Component
public class EventReceiver {

    @Bean
    public Consumer<Event<String, SpecificEvent>> messageProcessor() {
        return event -> {
            log.info("Received event: {} {}", event.getKey(), event.getData());
        };
    }
}
