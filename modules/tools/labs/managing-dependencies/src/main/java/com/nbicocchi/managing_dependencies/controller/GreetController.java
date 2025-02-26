package com.nbicocchi.managing_dependencies.controller;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
public class GreetController {
    @GetMapping("/greet")
    public String greet() {
        String response = "Hello world!";
        log.info("greet() invoked, returning {}", response);
        return response;
    }
}