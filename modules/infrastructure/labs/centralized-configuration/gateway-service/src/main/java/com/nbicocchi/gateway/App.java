package com.nbicocchi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class App {

	@Bean
	@LoadBalanced
	public RestClient.Builder loadBalancedRestClientBuilder() {
		return RestClient.builder();
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
