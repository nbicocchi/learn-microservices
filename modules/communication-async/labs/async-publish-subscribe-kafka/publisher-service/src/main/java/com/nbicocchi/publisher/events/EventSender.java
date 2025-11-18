package com.nbicocchi.publisher.events;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Log4j2
@Component
public class EventSender {
    private final StreamBridge streamBridge;

    public <K,V> void send(String bindingName, String key, Event<K, V> event) {
        log.info("Sending event: {}", event);
        Message<Event<K, V>> message = MessageBuilder.withPayload(event)
                .setHeader("routingKey", key)
                .setHeader("partitionKey", key)
                .build();
        streamBridge.send(bindingName, message);
    }
}