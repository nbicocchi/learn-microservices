package com.example.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(CheckConfig.class)
public class CheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckApplication.class, args);
	}

}
