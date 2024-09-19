package com.dime.ls.subscriber.handler;

import com.dime.ls.subscriber.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.io.IOException;

@Slf4j
@Component
public class EventSubscriber {

    private ObjectMapper objectMapper;

    private  ZMQ.Socket subscriber;

    @Autowired
    public EventSubscriber(ZMQ.Socket subscriber, ObjectMapper objectMapper) {

        this.subscriber = subscriber;
        this.objectMapper = objectMapper;

    }

    @Scheduled(fixedRate = 2500)
    @SneakyThrows(IOException.class)
    public void eventRetrieving(){

        String topic = subscriber.recvStr();
        log.info("Got event from topic: {}", topic);

        byte[] byteEvent = subscriber.recv();
        Event event = objectMapper.readValue(byteEvent, Event.class);
        log.info("Got event: {}", event);

    }
}
