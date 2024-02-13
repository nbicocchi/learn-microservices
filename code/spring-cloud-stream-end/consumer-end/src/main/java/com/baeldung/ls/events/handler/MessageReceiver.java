package com.baeldung.ls.events.handler;

import com.baeldung.ls.events.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    @Bean
    public Consumer<Event<String, Integer>> messageProcessor() {
        return event -> LOG.info(String.format("--> %s", event));
    }
}
