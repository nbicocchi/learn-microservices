package com.nbicocchi.echo.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class EchoController {
    @PostMapping(value = "/echo")
    public Map<String, Object> echo(@RequestBody String message) {
        return Map.of("echoed_data", message);
    }
}