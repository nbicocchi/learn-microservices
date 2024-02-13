package com.baeldung.ls.events.source;

import com.baeldung.ls.events.model.Event;
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
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);
    private final StreamBridge streamBridge;

    public MessageSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 1000)
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
        LOG.debug("Sending message {} to {}", event, bindingName);
        for (int i = 0; i < 5; i++) {
            Message<Event<String, Integer>> message = MessageBuilder.withPayload(event)
                    .setHeader("partitionKey", event.getKey())
                    .build();
            streamBridge.send(bindingName, message);
        }
    }
}
