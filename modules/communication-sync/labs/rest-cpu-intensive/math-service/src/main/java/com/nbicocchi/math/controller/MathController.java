package com.nbicocchi.math.controller;

import com.nbicocchi.math.service.MathService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j2
@RestController
@AllArgsConstructor
public class MathController {
    MathService mathService;

    /*
    echo 'GET http://localhost:8080/divisors?number=1234&times=40&email=test@test.com' | vegeta attack -rate=50 -duration=30s | vegeta report
     */
    @GetMapping("/divisors")
    public Map<String, Object> searchPrimes(
            @RequestParam Long number,
            @RequestParam Long times,
            @RequestParam String email) {

        List<Long> divisors = null;
        for (int i = 0; i < times; i++) {
            divisors = mathService.findPrimeDivisors(number);
        }

        return Map.of("divisors", divisors);
    }
}