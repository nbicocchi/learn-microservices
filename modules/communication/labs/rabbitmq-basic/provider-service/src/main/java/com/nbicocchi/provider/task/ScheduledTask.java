package com.nbicocchi.provider.task;

import com.nbicocchi.provider.model.Event;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
public class ScheduledTask {
    private final StreamBridge streamBridge;

    public ScheduledTask(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 1000)
    public void randomMessage() {
        Event<String, String> event = new Event<>(
                Event.Type.CREATE,
                UUID.randomUUID().toString(),
                "Hello from provider-service!"
        );
        sendMessage("message-out-0", event);
    }

    private void sendMessage(String bindingName, Event<String, String> event) {
        Message<Event<String, String>> message = MessageBuilder.withPayload(event).build();
        log.info("Sending message {} to {}", event, bindingName);
        streamBridge.send(bindingName, message);
    }
}
