package com.nbicocchi.proxy.controller;

import com.nbicocchi.proxy.event.Event;
import com.nbicocchi.proxy.event.EventMathRequest;
import com.nbicocchi.proxy.event.EventSender;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RestController
@AllArgsConstructor
public class ProxyController {
    private final EventSender eventSender;

    /*
    echo 'GET http://localhost:8080/divisors?number=1234&times=40&email=test@test.com' | vegeta attack -rate=50 -duration=30s | vegeta report
     */
    @GetMapping("/divisors")
    public EventMathRequest searchPrimes(
            @RequestParam Long number,
            @RequestParam Long times,
            @RequestParam String email) {

        EventMathRequest request = new EventMathRequest(number, times, email);
        Event<String, EventMathRequest> event = new Event<>(UUID.randomUUID().toString(), request);
        eventSender.send("message-out-0", event,
                new EventSender.Header<>("routingKey", "math.divisors"));

        return request;
    }

}