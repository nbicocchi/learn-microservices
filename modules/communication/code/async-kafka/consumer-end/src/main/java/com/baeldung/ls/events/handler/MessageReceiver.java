package com.baeldung.ls.events.handler;

import com.baeldung.ls.config.KafkaConfig;
import com.baeldung.ls.events.model.Event;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    @KafkaListener(topics = KafkaConfig.TOPIC_MESSAGES, groupId = KafkaConfig.GROUP_ID)
    public void receive(ConsumerRecord<String, Event<String, Integer>> record) {
        Event<String, Integer> event = record.value();
        int partition = record.partition();
        long offset = record.offset();

        switch (event.getEventType()) {
            case CREATE:
                LOG.info(String.format("[CREATE partition=%d offset=%d] --> %s", partition, offset, event));
                break;
            case DELETE:
                LOG.info(String.format("[DELETE partition=%d offset=%d] --> %s", partition, offset, event));
                break;
            case UPDATE:
                LOG.info(String.format("[UPDATE partition=%d offset=%d] --> %s", partition, offset, event));
                break;
            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE/DELETE/UPDATE event";
                throw new RuntimeException(errorMessage);
        }
    }
}
