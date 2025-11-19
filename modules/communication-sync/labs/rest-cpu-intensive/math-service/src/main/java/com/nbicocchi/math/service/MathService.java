package com.nbicocchi.math.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

@Service
public class MathService {

    public List<Long> findPrimeDivisors(long n) {
        List<Long> primes = new ArrayList<>();
        while (n % 2 == 0) { primes.add(2L); n /= 2; }
        for (long i = 3; i * i <= n; i += 2) {
            while (n % i == 0) { primes.add(i); n /= i; }
        }
        if (n > 2) primes.add(n);
        return primes;
    }

    public Long calculateMCD(List<Long> aFactors, List<Long> bFactors) {
        Map<Long, Long> aMap = countFactors(aFactors);
        Map<Long, Long> bMap = countFactors(bFactors);
        long mcd = 1L;
        for (Long factor : aMap.keySet()) {
            if (bMap.containsKey(factor)) {
                long minCount = Math.min(aMap.get(factor), bMap.get(factor));
                mcd *= (long) Math.pow(factor, minCount);
            }
        }
        return mcd;
    }

    private Map<Long, Long> countFactors(List<Long> factors) {
        Map<Long, Long> map = new HashMap<>();
        for (Long factor : factors) map.put(factor, map.getOrDefault(factor, 0L) + 1);
        return map;
    }
}
