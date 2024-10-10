package com.nbicocchi.events.events.handler;

import com.nbicocchi.events.events.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

@Configuration
public class EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private final StreamBridge streamBridge;

    public EventHandler(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void randomMessage() {
        int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
        Event<String, Integer> event = new Event(
                Event.Type.class.getEnumConstants()[index],
                UUID.randomUUID().toString(),
                RANDOM.nextInt(100)
        );
        sendMessage("message-out-0", event);
    }

    private void sendMessage(String bindingName, Event<String, Integer> event) {
        Message<Event<String, Integer>> message = MessageBuilder.withPayload(event).build();
        LOG.info("Sending message {} to {}", event, bindingName);
        streamBridge.send(bindingName, message);
    }

    @Bean
    public Consumer<Event<String, Integer>> requestReceiver() {
        return event -> {
            randomMessage();
            switch (event.getEventType()) {
                case FACT:
                    LOG.info(String.format("[FACT] --> %s", event));
                    break;
                case SQRT:
                    LOG.info(String.format("[SQRT] --> %s", event));
                    break;
                case POW2:
                    LOG.info(String.format("[POW2] --> %s", event));
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE/DELETE/UPDATE event";
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
