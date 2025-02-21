package com.nbicocchi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {
    public static void main(final String... args) {
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
        context.close();
    }
}
