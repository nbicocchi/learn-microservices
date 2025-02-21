package com.baeldung.raft;

import com.baeldung.raft.config.NodeConfig;
import com.baeldung.raft.config.TimeoutConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({NodeConfig.class, TimeoutConfig.class})
public class RaftImplementationWithSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaftImplementationWithSpringBootApplication.class, args);
    }
}
