package com.nbicocchi.composite.controller;

import com.nbicocchi.composite.model.TimeStampDTO;
import com.nbicocchi.composite.service.DateTimeIntegration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestController
public class CompositeController {
    DateTimeIntegration dateTimeIntegration;

    public CompositeController(DateTimeIntegration dateTimeIntegration, DateTimeIntegration blockingDateTimeIntegration) {
        this.dateTimeIntegration = dateTimeIntegration;
    }

    @GetMapping(value = "/time")
    public Map<String, LocalTime> time() {
        return Map.of("time", dateTimeIntegration.getTime());
    }

    @GetMapping(value = "/date")
    public Map<String, LocalDate> date() {
        return Map.of("date", dateTimeIntegration.getDate());
    }

    @GetMapping(value = "/datetime")
    public TimeStampDTO dateTime() {
        LocalTime localTime = dateTimeIntegration.getTime();
        LocalDate localDate = dateTimeIntegration.getDate();
        return new TimeStampDTO(localDate, localTime);
    }

    @GetMapping(value = "/timeBulkhead")
    public Map<String, LocalTime> timeBulkhead() {
        return Map.of("time", dateTimeIntegration.getTimeWithBulkhead());
    }

    @GetMapping(value = "/dateBulkhead")
    public Map<String, LocalDate> dateBulkhead() {
        return Map.of("date", dateTimeIntegration.getDateWithBulkhead());
    }
}
