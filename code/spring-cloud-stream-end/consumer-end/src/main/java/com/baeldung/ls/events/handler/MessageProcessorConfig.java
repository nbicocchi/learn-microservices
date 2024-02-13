package com.baeldung.ls.events.handler;

import com.baeldung.ls.events.model.OrganizationChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    @Bean
    public Consumer<OrganizationChangeModel> messageProcessor() {
        return event -> LOG.info(String.format("--> %s", event));
    }
}
