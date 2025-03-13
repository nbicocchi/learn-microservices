package com.nbicocchi.provider.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Log4j2
@RestController
public class GreetProviderController {
    private Long delay = 0L;

    @GetMapping("/greet")
    public Map<String, String> greet() throws InterruptedException {
        log.info("endpoint /greet() invoked");
        Thread.sleep(delay);
        return Map.of(
                "message", "hello world!",
                "timestamp", LocalDateTime.now().toString());
    }

    /**
     *  Use with:
     *  curl -X POST "http://localhost:8081/setDelay" -H "Content-Type: application/json" -d "3000"
     */
    @PostMapping("/setDelay")
    public void setDelay(@RequestBody Long delay) {
        this.delay = delay;
    }
}
