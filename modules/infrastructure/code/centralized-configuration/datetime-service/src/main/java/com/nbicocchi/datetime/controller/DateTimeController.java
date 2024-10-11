package com.nbicocchi.datetime.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class DateTimeController {
    @GetMapping(value = "/date")
    public LocalDate date() {
        return LocalDate.now();
    }

    @GetMapping(value = "/time")
    public LocalTime time() {
        return LocalTime.now();
    }
}
