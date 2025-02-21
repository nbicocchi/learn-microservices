package com.nbicocchi.sync_service.source;

import com.nbicocchi.sync_service.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
public class EventSenderImpl {
    private static final Logger LOG = LoggerFactory.getLogger(EventSenderImpl.class);
    private final StreamBridge streamBridge;

    public EventSenderImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void sendMessage(String bindingName, Event<String, Integer> event) {
        Message<Event<String, Integer>> message = MessageBuilder.withPayload(event).build();
        LOG.info("[SENDING] -> {} to {}", event, bindingName);
        streamBridge.send(bindingName, message);
    }
}
