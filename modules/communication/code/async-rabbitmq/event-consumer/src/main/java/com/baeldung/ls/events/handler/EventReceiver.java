package com.baeldung.ls.events.handler;

import com.baeldung.ls.events.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(EventReceiver.class);

    @Bean
    public Consumer<Event<String, Integer>> messageProcessor() {
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    LOG.info(String.format("[CREATE] --> %s", event));
                    break;
                case DELETE:
                    LOG.info(String.format("[DELETE] --> %s", event));
                    break;
                case UPDATE:
                    LOG.info(String.format("[UPDATE] --> %s", event));
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE/DELETE/UPDATE event";
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
