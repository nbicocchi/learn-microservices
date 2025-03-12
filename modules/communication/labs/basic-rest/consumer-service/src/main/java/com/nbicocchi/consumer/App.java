package com.nbicocchi.consumer;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Log4j2
@EnableScheduling
@SpringBootApplication
public class App {
	String providerServiceHost;
	int providerServicePort;

	public App(
			@Value("${app.provider-service.host}") String providerServiceHost,
			@Value("${app.provider-service.port}") int providerServicePort) {
		this.providerServiceHost = providerServiceHost;
		this.providerServicePort = providerServicePort;
	}

	@Scheduled(fixedRate = 2000)
	public void consume() {
		String endPoint = "http://" + providerServiceHost + ":" + providerServicePort + "/greet";
		RestClient restClient = RestClient.builder().build();
		Map<String, String> message = restClient.get()
				.uri(endPoint)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		log.info(message.toString());
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
