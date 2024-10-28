package com.valentini.compositeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration
public class ThymeleafConfig {

    /**
     * Configures the Spring Security dialect for Thymeleaf.
     *
     * @return the configured SpringSecurityDialect
     */
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}
