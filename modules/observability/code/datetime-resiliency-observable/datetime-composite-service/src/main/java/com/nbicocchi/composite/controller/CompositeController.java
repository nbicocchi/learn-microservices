package com.nbicocchi.composite.controller;

import com.nbicocchi.composite.model.LocalDateTimeWithTimestamp;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class CompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeController.class);
    DateTimeIntegration dateTimeIntegration;

    public CompositeController(DateTimeIntegration dateTimeIntegration, DateTimeIntegration blockingDateTimeIntegration) {
        this.dateTimeIntegration = dateTimeIntegration;
    }

    @GetMapping(value = "/time")
    public LocalTime time(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return dateTimeIntegration.getTime(delay, faultPercent);
    }

    @GetMapping(value = "/date")
    public LocalDate date(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return dateTimeIntegration.getDate(delay, faultPercent);
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
    public LocalTime timeBulkhead(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return dateTimeIntegration.getTimeWithBulkhead(delay, faultPercent);
    }

    @GetMapping(value = "/dateBulkhead")
    public LocalDate dateBulkhead(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return dateTimeIntegration.getDateWithBulkhead(delay, faultPercent);
    }
}
