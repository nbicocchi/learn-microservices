package com.nbicocchi.order.events;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Log4j2
@Component
public class EventSender {
    private final StreamBridge streamBridge;

    public <K,V> void send(String bindingName, String routingKey, Event<K, V> event) {
        log.info("Sending event: {}", event);
        Message<Event<K, V>> message = MessageBuilder.withPayload(event)
                .setHeader("routingKey", routingKey)
                .build();
        streamBridge.send(bindingName, message);
    }
}