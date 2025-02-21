package com.nbicocchi.events.events;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Log4j2
@Configuration
public class EventHandler {
    private final MessageSender messageSender;

    public EventHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Bean
    public Consumer<Event<String, Double>> mathRequest() {
        return event -> {
            log.info("[REQUEST] -> {}", event);
            Event<String, Double> response = new Event<>(
                    event.getEventType(),
                    event.getKey(),
                    0.0
            );
            switch (event.getEventType()) {
                case MUL10:
                    response.setData(event.getData() * 10);
                    messageSender.sendMessage("mathResponse-out-0", response);
                    break;
                case SQRT:
                    response.setData(Math.sqrt(event.getData()));
                    messageSender.sendMessage("mathResponse-out-0", response);
                    break;
                case POW2:
                    response.setData(Math.pow(event.getData(), 2));
                    messageSender.sendMessage("mathResponse-out-0", response);
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE/DELETE/UPDATE event";
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
