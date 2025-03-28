package com.nbicocchi.composite.controller;

import com.nbicocchi.composite.model.LocalDateTimeWithTimestamp;
import com.nbicocchi.composite.service.DateTimeIntegration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@RestController
public class CompositeController {
    DateTimeIntegration dateTimeIntegration;

    public CompositeController(DateTimeIntegration dateTimeIntegration, DateTimeIntegration blockingDateTimeIntegration) {
        this.dateTimeIntegration = dateTimeIntegration;
    }

    @GetMapping(value = "/time")
    public Map<String, LocalTime> time(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return Map.of("time", dateTimeIntegration.getTime(delay, faultPercent));
    }

    @GetMapping(value = "/date")
    public Map<String, LocalDate> date(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return Map.of("date", dateTimeIntegration.getDate(delay, faultPercent));
    }

    @GetMapping(value = "/datetime")
    public LocalDateTimeWithTimestamp dateTime(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        LocalTime localTime = dateTimeIntegration.getTime(delay, faultPercent);
        LocalDate localDate = dateTimeIntegration.getDate(delay, faultPercent);
        return new LocalDateTimeWithTimestamp(localDate, localTime, LocalDateTime.now());
    }

    @GetMapping(value = "/timeBulkhead")
    public Map<String, LocalTime> timeBulkhead(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return Map.of("time", dateTimeIntegration.getTimeWithBulkhead(delay, faultPercent));
    }

    @GetMapping(value = "/dateBulkhead")
    public Map<String, LocalDate> dateBulkhead(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return Map.of("date", dateTimeIntegration.getDateWithBulkhead(delay, faultPercent));
    }
}
