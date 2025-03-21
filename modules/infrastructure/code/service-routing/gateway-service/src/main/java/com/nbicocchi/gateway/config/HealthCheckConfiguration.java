package com.nbicocchi.gateway.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HealthCheckConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);
    private final RestClient restClient;

    public HealthCheckConfiguration(RestClient.Builder builder) {
        restClient = builder.build();
    }

    @Bean
    CompositeHealthContributor healthcheckMicroservices() {
        final Map<String, HealthIndicator> registry = new LinkedHashMap<>();
        registry.put("datetime", () -> getHealth("http://DATETIME-SERVICE"));
        registry.put("composite", () -> getHealth("http://COMPOSITE-SERVICE"));
        return CompositeHealthContributor.fromMap(registry);
    }

    private Health getHealth(String baseUrl) {
        String url = baseUrl + "/actuator/health";
        LOG.debug("Setting up a call to the Health API on URL: {}", url);
        String json = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
        // TODO: this is always up
        return new Health.Builder().up().build();
    }
}
