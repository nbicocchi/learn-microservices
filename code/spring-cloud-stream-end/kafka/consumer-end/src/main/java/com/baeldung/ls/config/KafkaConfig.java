package com.baeldung.ls.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaConfig.class);

    public static final String TOPIC_MESSAGES = "messages";
    public static final String TOPIC_MESSAGES_DLT = "messages.DLT";
    public static final String GROUP_ID = "messages-group";

    @Bean
    public NewTopic topicOrders() {
        return TopicBuilder.name(TOPIC_MESSAGES)
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersDLT() {
        return TopicBuilder.name(TOPIC_MESSAGES_DLT)
                .partitions(5)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "errorHandler", topics = TOPIC_MESSAGES_DLT)
    public void errorDLT(ConsumerRecord<?, ?> record) {
        LOG.info(String.format("DLT received: %s", record.value()));
    }

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
    }
}
