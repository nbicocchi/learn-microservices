package com.nbicocchi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class DateTimeController {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeController.class);

    @GetMapping("/time")
    public String getTime() {
        String response = LocalTime.now().toString();
        LOG.info("getTime() invoked, returning {}", response);
        return response;
    }

    @GetMapping("/date")
    public String getDate() {
        String response = LocalDate.now().toString();
        LOG.info("getDate() invoked, returning {}", response);
        return response;
    }
}