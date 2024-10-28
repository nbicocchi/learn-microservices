package com.valentini.userservice.config;

import com.valentini.userservice.security.ApiKeyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for simplicity; adjust as needed for your application
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Allow all requests; the filter will handle authorization
                        .anyRequest().permitAll()
                )
                // Add the custom API key filter before the authentication filter
                .addFilterBefore(new ApiKeyFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
