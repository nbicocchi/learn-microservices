package com.nbicocchi.datetime.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
public class DateTimeController {
    @Value("${app.default.zone}")
    String zoneId;

    @GetMapping(value = "/date")
    public LocalDate date() {
        LocalDate currentDate = LocalDate.now(ZoneId.of(zoneId));
        log.info("Returning date: {} (zone: {})", currentDate, zoneId);
        return currentDate;
    }

    @GetMapping(value = "/time")
    public LocalTime time() {
        LocalTime currentTime = LocalTime.now(ZoneId.of(zoneId));
        log.info("Returning time: {} (zone: {})", currentTime, zoneId);
        return currentTime;
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
}
