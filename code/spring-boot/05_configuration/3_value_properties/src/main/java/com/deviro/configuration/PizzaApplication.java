package com.deviro.configuration;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log
public class PizzaApplication implements CommandLineRunner {
    @Value("${pizza.sauce}")
    String sauce;
    @Value("${pizza.topping}")
    String topping;
    @Value("${pizza.crust}")
    String crust;

    public static void main(String[] args) {
        SpringApplication.run(PizzaApplication.class, args);
    }

    @Override
    public void run(final String... args) {
        log.info(String.format("I want a %s crust pizza, with %s and %s sauce", crust, topping, sauce));
    }
}
