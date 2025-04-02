package com.nbicocchi.datetime.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

@Log4j2
@RestController
public class DateTimeController {
    private static final RandomGenerator RND = RandomGenerator.getDefault();

    @Value("${app.default.zone}")
    String zoneId;

    @GetMapping(value = "/date")
    @RateLimiter(name = "date")
    public Map<String, LocalDate> date(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) throws InterruptedException {
        log.debug("invoked date()");
        Thread.sleep(delay);
        throwErrorIfBadLuck(faultPercent);
        return Map.of("date", LocalDate.now(ZoneId.of(zoneId)));
    }

    @GetMapping(value = "/time")
    @RateLimiter(name = "time")
    public Map<String, LocalTime> time(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) throws InterruptedException {
        log.debug("invoked time()");
        Thread.sleep(delay);
        throwErrorIfBadLuck(faultPercent);
        return Map.of("time", LocalTime.now(ZoneId.of(zoneId)));
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        if (RND.nextInt(0, 100) < faultPercent) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/zone")
    public Map<String, String> zone() {
        return Map.of("zone", zoneId);
    }

    @PostMapping(value = "/zone")
    public void zone(@RequestBody String zoneId) {
        if (ZoneId.getAvailableZoneIds().contains(zoneId)) {
            this.zoneId = zoneId;
        }
    }

    @GetMapping(value = "/zones", produces = "application/json")
    public List<String> zones() {
        List<String> zones = new ArrayList<>(ZoneId.getAvailableZoneIds());
        Collections.sort(zones);
        return zones;
    }
}
