package com.nbicocchi.events.events;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MessageSender {
    private final StreamBridge streamBridge;

    public MessageSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void sendMessage(String bindingName, Event<String, Double> event) {
        Message<Event<String, Double>> message = MessageBuilder.withPayload(event).build();
        log.info("[RESPONSE] -> {}", event);
        streamBridge.send(bindingName, message);
    }
}
