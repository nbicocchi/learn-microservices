package com.dime.ls.publisher.source;

import com.dime.ls.publisher.model.Event;
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
public class EventPublisher {

    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    private ObjectMapper objectMapper;

    private ZMQ.Socket publisher;

    @Autowired
    public EventPublisher(ZMQ.Socket publisher, ObjectMapper objectMapper){

        this.publisher = publisher;
        this.objectMapper = objectMapper;

    }

    @Scheduled(fixedRate = 1000, initialDelay = 8000)
    @SneakyThrows(JsonProcessingException.class)
    public void eventPublish() {

        Event event = Event.builder()
                .data("Hello World!")
                .key(UUID.randomUUID().toString())
                .eventType(Event.Type.class.getEnumConstants()[RANDOM.nextInt(Event.Type.class.getEnumConstants().length)])
                .build();

        String topic = "topic-" + RANDOM.nextInt(0,3);
        log.info("Choosen topic is {}", topic);

        String stringEvent = objectMapper.writeValueAsString(event);
        publisher.sendMore(topic.getBytes(ZMQ.CHARSET));
        publisher.send(stringEvent.getBytes(ZMQ.CHARSET));
        log.info("Event published: {}", stringEvent);

    }
}
