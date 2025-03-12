package com.nbicocchi.consumer.handler;

import com.nbicocchi.consumer.model.Event;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;

@Log4j2
@Configuration
public class EventReceiver {
    @Bean
    public Consumer<Event<String, String>> messageProcessor() {
        return event -> log.info("[RECEIVED] -> {}", event);
    }
}