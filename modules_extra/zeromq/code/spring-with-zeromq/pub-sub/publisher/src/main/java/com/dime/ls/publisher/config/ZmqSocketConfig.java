package com.dime.ls.publisher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

@Configuration
public class ZmqSocketConfig {

    @Value("${publisher.port}")
    private int publisherPort;

    @Value("${publisher.name}")
    private String publisherName;

    @Bean(destroyMethod = "close")
    public ZMQ.Socket zmqPublisher() {

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket publisher = context.socket(SocketType.PUB);

        String bindAddress = String.format("tcp://%s:%d", publisherName, publisherPort);
        publisher.bind(bindAddress);

        return publisher;
    }

}
