package com.nbicocchi.math.service;

import java.util.List;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class PrimeService {

    public List<Long> computePrimes(Long lowerBound, Long upperBound) {
        return LongStream.rangeClosed(lowerBound, upperBound)
                .filter(this::isPrime)
                .boxed()
                .collect(Collectors.toList());
    }

    private boolean isPrime(long number) {
        if (number < 2) return false;
        for (long i = 2; i * i <= number; i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}
