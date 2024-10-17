package com.nbicocchi.managing_dependencies.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class GreetControllerImpl implements GreetController{
    private static final Logger LOG = LoggerFactory.getLogger(GreetControllerImpl.class);

    @GetMapping("/greet")
    public String greet() {
        String response = "Hello world!";
        LOG.info("greet() invoked, returning {}", response);
        return response;
    }
}