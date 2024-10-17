package com.nbicocchi.lab1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface RedisController {
    @GetMapping("/set")
    String setValue(@RequestParam String key, @RequestParam String value);

    @GetMapping("/get")
    String getValue(@RequestParam String key);
}

