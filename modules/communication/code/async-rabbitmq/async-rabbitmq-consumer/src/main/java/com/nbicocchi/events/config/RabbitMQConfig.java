package com.nbicocchi.events.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("auditgroup")
public class RabbitMQConfig {

    @Value("${spring.cloud.stream.rabbit.bindings.messageProcessor-in-0.consumer.dead-letter-exchange}")
    private String deadLetterExchange;

    @Value("${spring.cloud.stream.rabbit.bindings.messageProcessor-in-0.consumer.dead-letter-queue-name}")
    private String deadLetterQueue;

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(deadLetterExchange);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(deadLetterQueue);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange());
    }
}
