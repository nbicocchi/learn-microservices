package com.nbicocchi.datetime.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.random.RandomGenerator;

@Log4j2
@RestController
public class DateTimeController {
    private final String zoneID = "Europe/Rome";
    private Integer delay = 0;
    private Integer faults = 0;

    @GetMapping(value = "/time")
    @RateLimiter(name = "time")
    public Map<String, LocalTime> time() throws InterruptedException {
        log.debug("invoked time()");
        Thread.sleep(delay);
        throwErrorIfBadLuck();
        return Map.of("time", LocalTime.now(ZoneId.of(zoneID)));
    }

    @GetMapping(value = "/date")
    public Map<String, LocalDate> date() throws InterruptedException {
        log.debug("invoked date()");
        Thread.sleep(delay);
        throwErrorIfBadLuck();
        return Map.of("date", LocalDate.now(ZoneId.of(zoneID)));
    }

    private void throwErrorIfBadLuck() {
        RandomGenerator RND = RandomGenerator.getDefault();
        if (RND.nextInt(0, 100) < faults) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/delay")
    public Map<String, String> SetDelay(@RequestBody Integer delay) {
        this.delay = delay;
        return Map.of("delay", delay.toString());
    }

    @PostMapping(value = "/faults")
    public Map<String, String> setFaults(@RequestBody Integer faults) {
        this.faults = faults;
        return Map.of("faults", faults.toString());
    }
}
