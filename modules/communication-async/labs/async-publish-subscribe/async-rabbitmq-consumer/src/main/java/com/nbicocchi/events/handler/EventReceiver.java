package com.nbicocchi.events.handler;

import com.nbicocchi.events.model.Event;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Log4j2
@Component
public class EventReceiver {
    @Bean
    public Consumer<Event<String, Integer>> messageProcessor() {
        return event -> {
            log.info(event.toString());

            switch (event.getType()) {
                case CREATE:
                    // do something
                    break;
                case DELETE:
                    // do something
                    break;
                case UPDATE:
                    // do something
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getType() + ", expected a CREATE/DELETE/UPDATE event";
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
