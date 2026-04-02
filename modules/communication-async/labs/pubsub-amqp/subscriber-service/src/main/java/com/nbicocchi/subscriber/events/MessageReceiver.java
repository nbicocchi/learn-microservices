package com.nbicocchi.subscriber.events;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageReceiver {

    //@RabbitListener(queues = "#{anonymousQueue.name}", containerFactory = "rabbitListenerContainerFactory")
    @RabbitListener(queues = "${QUEUE_NAME}", containerFactory = "rabbitListenerContainerFactory")
    public void listenNamed(String message, Channel channel, Message msg) throws IOException {
        processMessage(message, channel, msg, "namedQueue");
    }

    private void processMessage(String message, Channel channel, Message msg, String queueType) throws IOException {
        try {
            System.out.println("Received: " + message);

            // simulazione lavoro
            Thread.sleep(250);

            // ACK manuale
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // NACK e reinserimento in coda
            channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}