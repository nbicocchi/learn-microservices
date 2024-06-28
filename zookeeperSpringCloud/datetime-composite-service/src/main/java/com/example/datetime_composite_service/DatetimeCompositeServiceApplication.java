package com.example.datetime_composite_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DatetimeCompositeServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(DatetimeCompositeServiceApplication.class, args);
	}

}
