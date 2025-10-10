package com.nbicocchi;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Log4j2
@RestController
public class DateTimeController {
    @GetMapping("/time")
    public String getTime() {
        String response = LocalTime.now().toString();
        log.info("getTime() invoked, returning {}", response);
        return response;
    }

    @GetMapping("/date")
    public String getDate() {
        String response = LocalDate.now().toString();
        log.info("getDate() invoked, returning {}", response);
        return response;
    }
}