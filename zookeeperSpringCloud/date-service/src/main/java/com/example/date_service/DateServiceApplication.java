package com.example.date_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DateServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(DateServiceApplication.class, args);
	}

}
