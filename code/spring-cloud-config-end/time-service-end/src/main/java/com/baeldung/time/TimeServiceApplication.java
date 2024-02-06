package com.baeldung.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TimeServiceApplication {
	private static final Logger LOG = LoggerFactory.getLogger(TimeServiceApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(TimeServiceApplication.class, args);

		String profile = ctx.getEnvironment().getProperty("spring.profiles.active");
		LOG.info("Profile: " + profile);
	}
}
