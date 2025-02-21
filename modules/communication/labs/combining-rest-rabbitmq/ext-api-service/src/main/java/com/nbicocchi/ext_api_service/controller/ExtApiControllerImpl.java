package com.nbicocchi.ext_api_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.random.RandomGenerator;

@RestController
public class ExtApiControllerImpl implements ExtApiController {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private static final Logger LOG = LoggerFactory.getLogger(ExtApiControllerImpl.class);

    @GetMapping("/number")
    public Integer randomNumber() {
        Integer response = RANDOM.nextInt(100);
        LOG.info("[NUMBER GENERATED] -> {}", response);
        return response;
    }
}
