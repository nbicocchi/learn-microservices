package com.nbicocchi.math.controller;

import com.nbicocchi.math.dto.ProxyRequest;
import com.nbicocchi.math.service.PrimesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/compute")
public class MathController {
    PrimesService primeService;

    public MathController(PrimesService primeService) {
        this.primeService = primeService;
    }

    /**
     * curl -X POST "http://localhost:8081/compute" -H "Content-Type: application/json" -d '{ "lowerBound": 10, "upperBound": 1000, "email": "example@example.com" }'
     */
    @PostMapping()
    public Iterable<Long> searchPrimes(@RequestBody ProxyRequest request) {
        log.info("searchPrimes() request: {}", request);
        return primeService.computePrimes(
                request.lowerBound(),
                request.upperBound()
        );
    }
}