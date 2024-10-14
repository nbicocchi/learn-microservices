package com.nbicocchi.datetime.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

@RestController
public class DateTimeController {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeController.class);
    private static final RandomGenerator RND = RandomGenerator.getDefault();

    @Value("${app.default.zone}")
    String zoneId;

    @GetMapping(value = "/date")
    @RateLimiter(name = "dateRateLimiter")
    public LocalDate date(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) throws InterruptedException {
        Thread.sleep(delay);
        throwErrorIfBadLuck(faultPercent);
        return LocalDate.now(ZoneId.of(zoneId));
    }

    @GetMapping(value = "/time")
    @RateLimiter(name = "timeRateLimiter")
    public LocalTime time(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) throws InterruptedException {
        Thread.sleep(delay);
        throwErrorIfBadLuck(faultPercent);
        return LocalTime.now(ZoneId.of(zoneId));
    }

    @GetMapping(value = "/zone")
    public String zone() {
        return zoneId;
    }

    @PostMapping(value = "/zone")
    public void zone(@RequestBody String zoneId) {
        if (ZoneId.getAvailableZoneIds().contains(zoneId)) {
            this.zoneId = zoneId;
        }
    }

    @GetMapping(value="/zones", produces="application/json")
    public List<String> zones() {
        List<String> zones = new ArrayList<>(ZoneId.getAvailableZoneIds());
        Collections.sort(zones);
        return zones;
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        if (faultPercent == 0) return;
        int randomNumber = RND.nextInt(0, 100);
        if (randomNumber < faultPercent) {
            LOG.info("Bad luck, an error occurred, {} >= {}", faultPercent, randomNumber);
            throw new RuntimeException("Something went wrong...");
        }
    }
}
