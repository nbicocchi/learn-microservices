package com.nbicocchi.provider.source;

import com.nbicocchi.provider.model.Event;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.random.RandomGenerator;

@Log4j2
@Component
public class EventSender {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private final StreamBridge streamBridge;

    public EventSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 1000)
    public void randomMessage() {
        int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
        Event<String, String> event = new Event(
                Event.Type.class.getEnumConstants()[index],
                UUID.randomUUID().toString(),
                "Hello from provider-service!"
        );
        sendMessage("message-out-0", event);
    }

    public void sendMessage(String bindingName, Event<String, String> event) {
            Message<Event<String, String>> message = MessageBuilder.withPayload(event).build();
            log.info("[SENDING] -> {} to {}", event, bindingName);
            streamBridge.send(bindingName, message);
    }
}