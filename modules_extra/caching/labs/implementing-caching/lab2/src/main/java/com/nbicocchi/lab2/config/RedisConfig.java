package com.nbicocchi.lab2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

public interface RedisConfig {
    @Bean
    RedisConnectionFactory redisConnectionFactory();

    @Bean
    RedisTemplate<String, Object> redisTemplate();
}

