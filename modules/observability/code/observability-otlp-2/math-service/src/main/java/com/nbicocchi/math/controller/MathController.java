package com.nbicocchi.math.controller;

import com.nbicocchi.math.dto.DivisorsWithLatency;
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
public class MathController {
    @GetMapping("/divisors")
    public DivisorsWithLatency getPrimeDivisors(
            @RequestParam Long n,
            @RequestParam(defaultValue = "1000") Long times,
            @RequestParam(defaultValue = "0") Long faults) {
        log.trace("getPrimeDivisors({}-{})", n, times);

        // waste some time
        Long begin = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            findPrimeDivisors(n);
        }
        Long end = System.currentTimeMillis();

        // simulate eventual errors
        throwErrorIfBadLuck(faults);

        return new DivisorsWithLatency(n, findPrimeDivisors(n), end - begin);
    }

    private List<Long> findPrimeDivisors(long n) {
        List<Long> primes = new ArrayList<>();

        // Handle factor 2
        while (n % 2 == 0) {
            primes.add(2L);
            n /= 2;
        }

        // Handle odd factors
        for (long i = 3; i * i <= n; i += 2) {
            while (n % i == 0) {
                primes.add(i);
                n /= i;
            }
        }

        // If n is a prime > 2
        if (n > 2) {
            primes.add(n);
        }

        return primes;
    }

    private void throwErrorIfBadLuck(Long faults) {
        RandomGenerator RND = RandomGenerator.getDefault();
        if (RND.nextInt(0, 100) < faults) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}