package com.nbicocchi.math.controller;

import com.nbicocchi.math.dto.DivisorsWithLatency;
import com.nbicocchi.math.dto.MCDWithLatency;
import com.nbicocchi.math.service.MathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MathController {
    private final MathService mathService;

    /**
     * curl -X GET "http://localhost:8081/divisors?n=60&times=2&faults=10"
     */
    @GetMapping("/divisors")
    public DivisorsWithLatency getPrimeDivisors(@RequestParam Long n,
                                                @RequestParam(defaultValue = "1") Long times,
                                                @RequestParam(defaultValue = "0") Long faults) {
        log.trace("getPrimeDivisors({}-{})", n, times);

        Long begin = System.currentTimeMillis();
        List<Long> primes = null;
        for (int i = 0; i < times; i++) {
            primes = mathService.findPrimeDivisors(n);
        }
        Long end = System.currentTimeMillis();

        throwErrorIfBadLuck(faults);
        return new DivisorsWithLatency(n, primes, end - begin);
    }

    /**
     * curl -X GET "http://localhost:8081/mcd?a=60&b=48&aDivisors=2&aDivisors=2&aDivisors=3&bDivisors=2&bDivisors=2&bDivisors=2&bDivisors=3&times=1&faults=0"
     */
    @GetMapping("/mcd")
    public MCDWithLatency getMCD(@RequestParam Long a,
                                 @RequestParam Long b,
                                 @RequestParam List<Long> aDivisors,
                                 @RequestParam List<Long> bDivisors,
                                 @RequestParam(defaultValue = "1") Long times,
                                 @RequestParam(defaultValue = "0") Long faults) {
        log.trace("getMCD({} vs {})", a, b);

        Long begin = System.currentTimeMillis();
        Long mcd = 1L;
        for (int i = 0; i < times; i++) {
            mcd = mathService.calculateMCD(aDivisors, bDivisors);
        }
        Long end = System.currentTimeMillis();

        throwErrorIfBadLuck(faults);
        return new MCDWithLatency(a, b, aDivisors, bDivisors, mcd, end - begin);
    }

    private void throwErrorIfBadLuck(Long faults) {
        RandomGenerator RND = RandomGenerator.getDefault();
        if (RND.nextInt(0, 100) < faults) {
            throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
