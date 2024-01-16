package com.nbicocchi.microservices.composite.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
