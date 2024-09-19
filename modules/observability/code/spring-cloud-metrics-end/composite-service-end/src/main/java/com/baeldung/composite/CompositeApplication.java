package com.baeldung.composite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class CompositeApplication {

	@Autowired
	private ReactorLoadBalancerExchangeFilterFunction lbFunction;

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.filter(lbFunction).build();
	}

	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(CompositeApplication.class, args);
	}

}
