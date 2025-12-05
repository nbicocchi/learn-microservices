package com.nbicocchi.meter.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MeterController {

    @GetMapping(value = "/")
    public Map<String, Object> echo() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        return Map.of(
                "Available processors (cores)", Runtime.getRuntime().availableProcessors(),
                "Free memory (MB)", Runtime.getRuntime().freeMemory() / 1_000_000,
                "Maximum memory (MB)", maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / 1_000_000,
                "Total memory (MB)", Runtime.getRuntime().totalMemory() / 1_000_000,
                "Allocated memory (MB)", (totalMemory - freeMemory) / 1_000_000
        );
    }

    @GetMapping(value = "/allocate/{size}")
    public Map<String, Object> allocate(@PathVariable Integer size) {
        try {
            byte[] array = new byte[size * 1_000_000];
            return Map.of("array allocation", "OK");
        } catch (OutOfMemoryError e) {
            return Map.of("array allocation", "Out of memory");
        }
    }
}