package com.baeldung.time;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class TimeController {

    @GetMapping(value = "/time")
    public LocalTime time() throws InterruptedException {
        Thread.sleep(1000);
        return LocalDateTime.now().toLocalTime();
    }
}
