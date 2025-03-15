package com.nbicocchi.proxy.controller;

import com.nbicocchi.proxy.model.Event;
import com.nbicocchi.proxy.model.ProxyRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/compute")
public class ProxyController {
    private final StreamBridge streamBridge;

    public ProxyController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * curl -X POST "http://localhost:8080/compute" -H "Content-Type: application/json" -d '{ "lowerBound": 10, "upperBound": 1000, "email": "example@example.com" }'
     */
    @PostMapping()
    public ProxyRequest searchPrimes(@RequestBody ProxyRequest request) {
        Event<UUID, ProxyRequest> event = new Event<>(
                Event.Type.CREATE,
                UUID.randomUUID(),
                request
        );
        sendMessage("message-out-0", event);
        log.info("[SENT] -> {}", event);
        return request;
    }

    private void sendMessage(String bindingName, Event<UUID, ProxyRequest> event) {
        Message<Event<UUID, ProxyRequest>> message = MessageBuilder.withPayload(event).build();
        streamBridge.send(bindingName, message);
    }
}