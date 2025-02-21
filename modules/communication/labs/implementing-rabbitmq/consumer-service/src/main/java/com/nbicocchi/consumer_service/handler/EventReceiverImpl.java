package com.nbicocchi.consumer_service.handler;

import com.nbicocchi.consumer_service.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;

@Configuration
public class EventReceiverImpl implements EventReceiver{
    private static final Logger LOG = LoggerFactory.getLogger(EventReceiverImpl.class);

    @Bean
    public Consumer<Event<String, String>> messageProcessor() {
        return event -> LOG.info("[RECEIVED] -> {}", event);
    }
}