package com.nbicocchi.dummy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.random.RandomGenerator;

@RestController
public class DummyController {
    private static final Logger LOG = LoggerFactory.getLogger(DummyController.class);
    private static final RandomGenerator RND = RandomGenerator.getDefault();

    @GetMapping(value = "/ok")
    public String ok() {
        LOG.info("Good luck, returning OK");
        return "OK";
    }

    @GetMapping(value = "/notOk")
    public String notOK() {
        throwErrorIfBadLuck(100);
        return ok();
    }

    @GetMapping(value = "/mayFail")
    public String mayFail() {
        throwErrorIfBadLuck(50);
        return ok();
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        if (faultPercent == 0) return;
        int randomNumber = RND.nextInt(0, 100);
        if (randomNumber < faultPercent) {
            LOG.info("Bad luck, returning RuntimeException");
            throw new RuntimeException("Something went wrong...");
        }
    }
}
