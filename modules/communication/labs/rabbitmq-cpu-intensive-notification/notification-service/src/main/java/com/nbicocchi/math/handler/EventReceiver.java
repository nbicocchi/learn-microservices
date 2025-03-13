package com.nbicocchi.math.handler;

import com.nbicocchi.math.model.Event;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Log4j2
@Component
public class EventReceiver {
    @Bean
    public Consumer<Event<String, Iterable<Long>>> processNotification() {
        return event -> {
            log.info("Primes: {}", event);
        };
    }
}