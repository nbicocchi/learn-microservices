package com.nbicocchi.notification.event;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Log4j2
@Component
@AllArgsConstructor
public class EventReceiver {
    @Bean
    public Consumer<Event<String, EventNotification>> processNotification() {
        return event -> {
            log.info(event.toString());
        };
    }
}