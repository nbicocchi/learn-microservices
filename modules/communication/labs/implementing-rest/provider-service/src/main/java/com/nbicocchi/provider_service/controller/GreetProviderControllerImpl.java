package com.nbicocchi.provider_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetProviderControllerImpl {

    @GetMapping("/greet")
    public String greet() {
        return "Hello!";
    }
}
