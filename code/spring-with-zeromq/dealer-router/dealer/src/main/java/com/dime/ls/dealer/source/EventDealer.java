package com.dime.ls.dealer.source;

import com.dime.ls.dealer.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

import java.util.UUID;
import java.util.random.RandomGenerator;

@Slf4j
@Component
public class EventDealer {

    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    private ObjectMapper objectMapper;

    private ZMQ.Socket dealer;

    @Autowired
    public EventDealer(ZMQ.Socket dealer, ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.dealer = dealer;

    }

    @Scheduled(fixedRate = 1000)
    @SneakyThrows(JsonProcessingException.class)
    public void eventProduction() {
        Event event = Event.builder()
                .data("Hello World!")
                .key(UUID.randomUUID().toString())
                .eventType(Event.Type.class.getEnumConstants()[RANDOM.nextInt(Event.Type.class.getEnumConstants().length)])
                .build();
        String stringEvent = objectMapper.writeValueAsString(event);
        log.info("Sent: {}", stringEvent);
        dealer.send(stringEvent);
    }
}