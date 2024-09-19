package com.dime.ls.router.handler;

import com.dime.ls.router.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

@Slf4j
@Component
public class EventRouter {

    private ObjectMapper objectMapper;

    private ZMQ.Socket router;

    @Autowired
    public EventRouter(ZMQ.Socket router, ObjectMapper objectMapper) {

        this.router = router;

        this.objectMapper = objectMapper;

    }

    @Scheduled(fixedRate = 2500)
    @SneakyThrows(JsonProcessingException.class)
    public void eventHandling() {

        ZMsg msg = ZMsg.recvMsg(router);

        String sock_identity = new String(msg.pop().getData(), ZMQ.CHARSET);
        //log.info("Identity: {}", sock_identity);
        String message = new String(msg.pop().getData(), ZMQ.CHARSET);
        Event event = objectMapper.readValue(message, Event.class);
        log.info("Received: {}", event);
    }

}
