package com.nbicocchi.publisher.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "topic.queue.1";
    public static final String ROUTING_KEY = "foo.#";
    public static final String EXCHANGE_NAME = "myExchange";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        // this queue receives messages with routing key "foo.*"
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

}
