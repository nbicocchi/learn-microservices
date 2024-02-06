package com.baeldung.ls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;

import java.util.Random;
import java.util.random.RandomGenerator;

@Component
public class MessageSender {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);
    private final StreamBridge streamBridge;

    public MessageSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 1000)
    public void randomMessage() {
        sendMessage("message-out-0", Integer.toString(RANDOM.nextInt(100)));
    }

    private void sendMessage(String bindingName, String event) {
        LOG.debug("Sending message {} to {}", event, bindingName);
        Message<String> message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event)
                .build();
        streamBridge.send(bindingName, message);
    }
}
