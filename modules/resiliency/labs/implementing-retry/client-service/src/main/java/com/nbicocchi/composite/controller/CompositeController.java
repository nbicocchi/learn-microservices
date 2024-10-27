package com.nbicocchi.composite.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeController.class);
    DummyIntegration dummyIntegration;

    public CompositeController(DummyIntegration dummyIntegration) {
        this.dummyIntegration = dummyIntegration;
    }

    @GetMapping(value = "/ok")
    public String ok() {
        return dummyIntegration.getOk();
    }

    @GetMapping(value = "/notOk")
    public String notOk() {
        return dummyIntegration.getNotOk();
    }

    @GetMapping(value = "/mayFail")
    public String mayFail() {
        return dummyIntegration.mayFail();
    }

}
