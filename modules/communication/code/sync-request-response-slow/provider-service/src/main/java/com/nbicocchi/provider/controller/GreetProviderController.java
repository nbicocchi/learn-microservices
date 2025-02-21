package com.nbicocchi.provider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetProviderController {
    private Long delay = 0L;

    @GetMapping("/greet")
    public String greet() throws InterruptedException {
        Thread.sleep(delay);
        return "Hello!";
    }

    @PostMapping("/setDelay")
    public void setDelay(@RequestBody Long delay) {
        this.delay = delay;
    }
}
