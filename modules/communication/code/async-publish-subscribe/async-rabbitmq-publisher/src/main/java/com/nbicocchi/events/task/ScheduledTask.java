package com.nbicocchi.events.task;

import com.nbicocchi.events.model.Event;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Log4j2
@Component
public class ScheduledTask {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private final StreamBridge streamBridge;
    private final Set<UUID> uuids = new HashSet<>();

    public ScheduledTask(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 5; i++) {
            uuids.add(UUID.randomUUID());
        }
    }

    @Scheduled(fixedRate = 100)
    public void randomMessage() {
        Event.Type randomType = Event.Type.class.getEnumConstants()[RANDOM.nextInt(Event.Type.class.getEnumConstants().length)];
        UUID randomUUID = (UUID) uuids.toArray()[RANDOM.nextInt(uuids.size())];
        Integer randomData = RANDOM.nextInt(100);

        Event<UUID, Integer> event = new Event<>(
                randomType,
                randomUUID,
                randomData
        );

        sendMessage("message-out-0", event);
        log.info(event.toString());
    }

    private void sendMessage(String bindingName, Event<UUID, Integer> event) {
        Message<Event<UUID, Integer>> message = MessageBuilder.withPayload(event)
                .setHeader("routingKey", event.getType().name())
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }
}
