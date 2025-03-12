package com.nbicocchi.proxy.source;

import com.nbicocchi.proxy.model.Event;
import com.nbicocchi.proxy.model.ProxyRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class EventSender {
    private final StreamBridge streamBridge;

    public EventSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void sendMessage(String bindingName, Event<String, ProxyRequest> event) {
            Message<Event<String, ProxyRequest>> message = MessageBuilder.withPayload(event).build();
            log.info("[SENDING] -> {} to {}", event, bindingName);
            streamBridge.send(bindingName, message);
    }
}