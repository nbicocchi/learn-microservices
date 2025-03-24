package com.nbicocchi.datetime.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DateTimeController {
    @Value("${app.default.zone}")
    String zoneId;

    @GetMapping(value = "/date")
    public Map<String, LocalDate> date() {
        return Map.of("LocalDate", LocalDate.now(ZoneId.of(zoneId)));
    }

    @GetMapping(value = "/time")
    public Map<String, LocalTime> time() {
        return Map.of("LocalTime", LocalTime.now(ZoneId.of(zoneId)));
    }

    @GetMapping(value = "/zone")
    public Map<String, String> zone() {
        return Map.of("zoneId", zoneId);
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
        return zones.stream().sorted().collect(Collectors.toList());
    }
}
