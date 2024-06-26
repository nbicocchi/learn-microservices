package com.example.date_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class DateController {

    @GetMapping("/date")
    public String getCurrentDate() {

        return LocalDate.now().toString();
    }

    @Value("${configuration}")
    private String configuration;

    @GetMapping("/configuration")
    public String getMessage() {

        return this.configuration;
    }
}
