package com.nbicocchi.events.task;

import com.nbicocchi.events.model.Event;
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
public class ScheduledTask {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private final StreamBridge streamBridge;

    public ScheduledTask(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 1000)
    public void randomMessage() {
        int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
        Event<String, Integer> event = new Event<>(
                Event.Type.class.getEnumConstants()[index],
                UUID.randomUUID().toString(),
                RANDOM.nextInt(100)
        );

        // send 5 copies of the same message
        for (int i = 0; i < 5; i++) {
            sendMessage("message-out-0", event);
        }
    }

    private void sendMessage(String bindingName, Event<String, Integer> event) {
        Message<Event<String, Integer>> message = MessageBuilder.withPayload(event)
                .setHeader("routingKey", event.getEventType().name())
                .setHeader("partitionKey", event.getKey())
                .build();
        log.info("Sending message {} to {}", event, bindingName);
        streamBridge.send(bindingName, message);
    }
}
