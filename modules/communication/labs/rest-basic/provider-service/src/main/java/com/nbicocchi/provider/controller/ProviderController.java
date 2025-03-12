package com.nbicocchi.provider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProviderController {

    @GetMapping("/greet")
    public Map<String, String> greet() {
        return Map.of("message", "hello world!");
    }
}
