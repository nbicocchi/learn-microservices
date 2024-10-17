package com.nbicocchi.producer_service.source;

import com.nbicocchi.producer_service.model.Event;
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
public class EventSenderImpl {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private static final Logger LOG = LoggerFactory.getLogger(EventSenderImpl.class);
    private final StreamBridge streamBridge;

    public EventSenderImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 1000)
    public void randomMessage() {
        int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
        Event<String, String> event = new Event(
                Event.Type.class.getEnumConstants()[index],
                UUID.randomUUID().toString(),
                "Hello from producer"
        );
        sendMessage("message-out-0", event);
    }

    public void sendMessage(String bindingName, Event<String, String> event) {
            Message<Event<String, String>> message = MessageBuilder.withPayload(event).build();
            LOG.info("[SENDING] -> {} to {}", event, bindingName);
            streamBridge.send(bindingName, message);
    }
}