package com.nbicocchi.publisher.events;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {

    void sendToMqtt(
            String data,
            @Header(MqttHeaders.TOPIC) String topic
    );
}