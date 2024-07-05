package com.baeldung.composite;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class CompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeController.class);
    DateTimeIntegration dateTimeIntegration;
    BlockingDateTimeIntegration blockingDateTimeIntegration;

    public CompositeController(DateTimeIntegration dateTimeIntegration, BlockingDateTimeIntegration blockingDateTimeIntegration) {
        this.dateTimeIntegration = dateTimeIntegration;
        this.blockingDateTimeIntegration = blockingDateTimeIntegration;
    }

    @GetMapping(value = "/datetime/time")
    public Mono<LocalTime> dateTime(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return dateTimeIntegration.getTimeWithResilience(delay, faultPercent);
    }

    @GetMapping(value = "/datetime/timeRaw")
    public LocalTime dateTimeRaw(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return blockingDateTimeIntegration.getTime(delay, faultPercent);
    }

    @GetMapping(value = "/datetime/date")
    public LocalDate date(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    ) {
        return blockingDateTimeIntegration.getDate(delay, faultPercent);
    }
}
