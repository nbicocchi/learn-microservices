package com.nbicocchi.math.handler;

import com.nbicocchi.math.model.Event;
import com.nbicocchi.math.model.ProxyRequest;
import com.nbicocchi.math.service.PrimesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Log4j2
@Component
public class EventReceiver {
    private final StreamBridge streamBridge;
    private final PrimesService primeService;

    public EventReceiver(StreamBridge streamBridge, PrimesService primeService) {
        this.streamBridge = streamBridge;
        this.primeService = primeService;
    }

    @Bean
    public Consumer<Event<String, ProxyRequest>> primeProcessor() {
        return event -> {
            log.info("[RECV] -> {}", event);
            List<Long> primes = primeService.computePrimes(
                    event.getData().lowerBound(),
                    event.getData().upperBound());

            Event<String, Iterable<Long>> newEvent = new Event<>(
                    UUID.randomUUID().toString(),
                    primes
            );
            sendMessage("primeProcessor-out-0", newEvent);
            log.info("[SENT] -> {}", newEvent);
        };
    }

    private void sendMessage(String bindingName, Event<String, Iterable<Long>> event) {
        Message<Event<String, Iterable<Long>>> message = MessageBuilder.withPayload(event).build();
        streamBridge.send(bindingName, message);
    }
}