package com.dime.ls.router.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

@Configuration
public class ZmqSocketConfig {

    @Value("${router.port}")
    private int routerPort;

    @Value("${router.name}")
    private String routerName;

    @Bean(destroyMethod = "close")
    public ZMQ.Socket zmqRouter(){

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket router = context.socket(SocketType.ROUTER);
        String bindAddress = String.format("tcp://%s:%d", routerName, routerPort);
        router.bind(bindAddress);

        return router;

    }

}
