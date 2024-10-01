package com.baeldung.ls.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@PropertySource("classpath:additional.properties")
@Configuration
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    private void postConstruct(){
        LOG.info("project prefix: {}", environment.getProperty("project.prefix"));
        LOG.info("project suffix: {}", environment.getProperty("project.suffix"));
    }
}
