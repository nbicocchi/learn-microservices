package com.nbicocchi.managing_dependencies.controller;

import org.springframework.web.bind.annotation.GetMapping;

public interface GreetController {
    @GetMapping("/greet")
    String greet();
}