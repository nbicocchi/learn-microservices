package com.nbicocchi.subscriber.events;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class MessageReceiver {

    @RabbitListener(
            queues = "${QUEUE_NAME}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void listenNamed(String message, Channel channel, Message msg) throws IOException {
        processMessage(message, channel, msg, "namedQueue");
    }

    private void processMessage(String message,
                                Channel channel,
                                Message msg,
                                String queueType) throws IOException {

        long tag = msg.getMessageProperties().getDeliveryTag();

        try {
            System.out.print("Received: " + message);

            // work
            Thread.sleep(250);

            // simulazione errore random
            if (Math.random() < 0.05) {
                throw new RuntimeException("Random failure");
            }

            System.out.println("[OK]");
            channel.basicAck(tag, false);

        } catch (Exception e) {

            // manda in DLQ (se configurata)
            System.err.println("[FAIL]");
            channel.basicNack(tag, false, false);
        }
    }
}