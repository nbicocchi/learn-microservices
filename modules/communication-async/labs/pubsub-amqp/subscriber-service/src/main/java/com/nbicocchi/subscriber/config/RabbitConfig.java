package com.nbicocchi.subscriber.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Log4j2
@Configuration
public class RabbitConfig {

    @Value("${QUEUE_NAME}")
    private String queueName;

    @Value("${ROUTING_KEY}")
    private String routingKey;

    @Value("${EXCHANGE_NAME}")
    private String exchangeName;

    // ---------------- EXCHANGE ----------------

    @Bean
    public TopicExchange exchange() {
        log.info("Creating Exchange: {}", exchangeName);
        return new TopicExchange(exchangeName);
    }

    // ---------------- MAIN QUEUE ----------------

    @Bean
    public Queue queue() {

        log.info("Creating Queue: {}", queueName);

        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxExchange().getName())
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey())
                .build();
    }

    // ---------------- DLQ EXCHANGE ----------------

    @Bean
    public DirectExchange dlxExchange() {
        String name = exchangeName + ".dlx";
        log.info("Creating DLX Exchange: {}", name);
        return new DirectExchange(name);
    }

    // ---------------- DLQ QUEUE ----------------

    @Bean
    public Queue deadLetterQueue() {
        String dlqName = queueName + ".dlq";
        log.info("Creating DLQ Queue: {}", dlqName);
        return QueueBuilder.durable(dlqName).build();
    }

    // ---------------- DLQ ROUTING KEY ----------------

    @Bean
    public String dlqRoutingKey() {
        return queueName + ".dlq";
    }

    // ---------------- DLQ BINDING ----------------

    @Bean
    public Binding dlqBinding() {
        log.info("Creating DLQ Binding");

        return BindingBuilder
                .bind(deadLetterQueue())
                .to(dlxExchange())
                .with(dlqRoutingKey());
    }

    // ---------------- MAIN BINDING ----------------

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {

        Binding binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(routingKey);

        log.info("Creating Binding:");
        log.info("  Queue       : {}", queue.getName());
        log.info("  Exchange    : {}", exchange.getName());
        log.info("  Routing Key : {}", routingKey);

        return binding;
    }

    // ---------------- LISTENER FACTORY ----------------

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setPrefetchCount(1); // fair dispatch
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        log.info("RabbitListenerContainerFactory configured: prefetch=1, manual-ack");

        return factory;
    }
}