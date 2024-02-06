package com.baeldung.date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class DateController {

    @GetMapping(value = "/date")
    public LocalDate time() throws InterruptedException {
        Thread.sleep(1000);
        return LocalDateTime.now().toLocalDate();
    }
}
