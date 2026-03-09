package com.nbicocchi.composite.controller;

import com.nbicocchi.composite.dto.LocalDateTimeWithInfo;
import com.nbicocchi.composite.service.DBIntegration;
import com.nbicocchi.composite.service.DateTimeIntegration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestController
public class CompositeController {
    DBIntegration dbIntegration;
    DateTimeIntegration dateTimeIntegration;

    public CompositeController(DBIntegration dbIntegration, DateTimeIntegration dateTimeIntegration) {
        this.dbIntegration = dbIntegration;
        this.dateTimeIntegration = dateTimeIntegration;
    }

    @GetMapping(value = "/testLatency")
    public LocalDateTimeWithInfo testLatency(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) throws InterruptedException {
        Thread.sleep(delay);
        LocalTime localTime = LocalTime.now();
        LocalDate localDate = LocalDate.now();
        return new LocalDateTimeWithInfo(localDate, localTime, "n/a");
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

    @GetMapping(value = "/datetime")
    public LocalDateTimeWithInfo dateTime(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        LocalTime localTime = dateTimeIntegration.getTime(delay, faultPercent);
        LocalDate localDate = dateTimeIntegration.getDate(delay, faultPercent);
        return new LocalDateTimeWithInfo(localDate, localTime, "n/a");
    }

    @GetMapping(value = "/datetimeInfo")
    public LocalDateTimeWithInfo datetimeInfo(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        LocalTime localTime = dateTimeIntegration.getTime(delay, faultPercent);
        LocalDate localDate = dateTimeIntegration.getDate(delay, faultPercent);
        String info = dbIntegration.getInfosWithCache(localDate.getMonthValue(), localDate.getDayOfMonth());
        return new LocalDateTimeWithInfo(localDate, localTime, info);
    }
}
