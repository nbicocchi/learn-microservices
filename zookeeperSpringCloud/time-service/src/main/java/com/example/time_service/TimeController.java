package com.example.time_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class TimeController {

    @GetMapping("/time")
    public String getCurrentTime() {

        return LocalTime.now().toString();
    }

    @Value("${configuration}")
    private String configuration;

    @GetMapping("/configuration")
    public String getMessage() {

        return this.configuration;
    }
}
