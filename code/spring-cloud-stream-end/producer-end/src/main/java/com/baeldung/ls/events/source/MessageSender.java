package com.baeldung.ls.events.source;

import com.baeldung.ls.events.model.OrganizationChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
        List<String> types = Arrays.asList("CREATE", "UPDATE", "DELETE");
        OrganizationChangeModel event = new OrganizationChangeModel(
                types.get(RANDOM.nextInt(3)),
                UUID.randomUUID().toString()
        );
        sendMessage("message-out-0", event);
    }

    private void sendMessage(String bindingName, OrganizationChangeModel event) {
        LOG.debug("Sending message {} to {}", event, bindingName);
        Message<OrganizationChangeModel> message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event)
                .build();
        streamBridge.send(bindingName, message);
    }
}
