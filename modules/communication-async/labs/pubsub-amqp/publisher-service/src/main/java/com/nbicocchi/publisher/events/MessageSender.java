package com.nbicocchi.publisher.events;

import com.nbicocchi.publisher.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    private final RabbitTemplate rabbitTemplate;

    @Value("${EXCHANGE_NAME}")
    private String exchangeName;

    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message, String routingKey) {
        rabbitTemplate.convertAndSend(
                exchangeName,   // ora variabile, non più costante
                routingKey,
                message
        );
    }
}
