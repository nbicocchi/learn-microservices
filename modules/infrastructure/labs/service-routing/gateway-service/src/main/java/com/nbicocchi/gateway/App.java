package com.nbicocchi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("bff-service", r -> r.path("/bff/**")
						.uri("lb://bff-service"))
				.route("user-service", r -> r.path("/users/**")
						.uri("lb://user-service"))
				.route("post-service", r -> r.path("/posts/**")
						.uri("lb://post-service"))
				.route("comment-service", r -> r.path("/comments/**")
						.uri("lb://comment-service"))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
