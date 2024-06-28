package com.example.time_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TimeServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(TimeServiceApplication.class, args);
	}

}
