package com.nbicocchi.dummy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

@RestController
public class DummyController {
    private static final Logger LOG = LoggerFactory.getLogger(DummyController.class);

    @GetMapping(value = "/ok")
    public String ok() {
        LOG.info("Good luck, returning OK");
        return "OK";
    }

    @GetMapping(value = "/notOk")
    public String notOK() {
        LOG.info("Bad luck, returning RuntimeException");
        throw new RuntimeException("Something went wrong...");
    }


}
