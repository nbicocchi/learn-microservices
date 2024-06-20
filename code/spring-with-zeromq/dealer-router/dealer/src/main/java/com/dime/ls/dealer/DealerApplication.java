package com.dime.ls.dealer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.dime")
@EnableScheduling
public class DealerApplication {

	public static void main(String[] args) { SpringApplication.run(DealerApplication.class, args); }

}
