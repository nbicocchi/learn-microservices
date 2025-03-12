package com.nbicocchi.events.handler;

import com.nbicocchi.events.model.Event;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Log4j2
@Component
public class EventReceiver {
    @Bean
    public Consumer<Event<String, Integer>> messageProcessor() {
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    log.info("[CREATE] --> {}", event);
                    break;
                case DELETE:
                    log.info("[DELETE] --> {}", event);
                    break;
                case UPDATE:
                    log.info("[UPDATE] --> {}", event);
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE/DELETE/UPDATE event";
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
