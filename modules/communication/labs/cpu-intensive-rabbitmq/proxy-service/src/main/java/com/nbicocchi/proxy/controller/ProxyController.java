package com.nbicocchi.proxy.controller;

import com.nbicocchi.proxy.model.Event;
import com.nbicocchi.proxy.model.ProxyRequest;
import com.nbicocchi.proxy.source.EventSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/compute")
public class ProxyController {
    EventSender eventSender;

    public ProxyController(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    /**
     * curl -X POST "http://localhost:8080/compute" -H "Content-Type: application/json" -d '{ "lowerBound": 10, "upperBound": 1000, "email": "example@example.com" }'
     */
    @PostMapping()
    public ProxyRequest searchPrimes(@RequestBody ProxyRequest request) {
        Event<String, ProxyRequest> event = new Event<>(
                UUID.randomUUID().toString(),
                request
        );
        eventSender.sendMessage("message-out-0", event);
        return request;
    }
}