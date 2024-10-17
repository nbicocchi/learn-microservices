package com.nbicocchi.lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class RedisLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisLabApplication.class, args);
	}

}
