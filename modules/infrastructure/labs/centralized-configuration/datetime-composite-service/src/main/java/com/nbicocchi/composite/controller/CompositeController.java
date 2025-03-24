package com.nbicocchi.composite.controller;

import com.nbicocchi.composite.integration.DateTimeIntegration;
import com.nbicocchi.composite.model.LocalDateTimeWithTimestamp;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class CompositeController {
    DateTimeIntegration dateTimeIntegration;

    public CompositeController(DateTimeIntegration dateTimeIntegration) {
        this.dateTimeIntegration = dateTimeIntegration;
    }

    @GetMapping(value = "/datetime")
    public LocalDateTimeWithTimestamp dateTime() {
        LocalDate date = dateTimeIntegration.getDate();
        LocalTime time = dateTimeIntegration.getTime();
        return new LocalDateTimeWithTimestamp(date, time, LocalDateTime.now());
    }
}
