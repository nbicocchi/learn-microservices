package com.dime.ls.dealer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

@Configuration
public class ZmqSocketConfig {

    @Value("${IDENTITY}")
    private String identity;
    
    @Value("${ROUTERS_NAMES}")
    private String routersNames;
    
    @Value("${DELIMITER}")
    private String delimiter;

    @Value("${router.port}")
    private int routerPort;
    
    @Bean(destroyMethod = "close")
    public ZMQ.Socket zmqDealer(){

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket dealer = context.socket(SocketType.DEALER);
        
        dealer.setIdentity(this.identity.trim().getBytes(ZMQ.CHARSET));
        String[] routersNamesArray = this.routersNames.split(delimiter);

        for (String router : routersNamesArray) {
            String connectAddress = String.format("tcp://%s:%d", router.trim(), routerPort);
            dealer.connect(connectAddress);
        }

        return dealer;

    }
}
