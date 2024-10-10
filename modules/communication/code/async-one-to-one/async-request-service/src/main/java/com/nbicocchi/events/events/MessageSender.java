package com.nbicocchi.events.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.random.RandomGenerator;

@Component
public class MessageSender {
    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);
    private final StreamBridge streamBridge;

    public MessageSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void sendMessage(String bindingName, Event<String, Integer> event) {
        Message<Event<String, Integer>> message = MessageBuilder.withPayload(event).build();
        LOG.info("[REQUEST] -> {}", event);
        streamBridge.send(bindingName, message);
    }
}
