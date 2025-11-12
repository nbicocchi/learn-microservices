package com.nbicocchi.gateway.controller;

import com.nbicocchi.gateway.dto.DivisorsWithLatency;
import com.nbicocchi.gateway.dto.MCDWithLatency;
import com.nbicocchi.gateway.service.MathIntegration;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST che funge da gateway per i servizi matematici esposti.
 * Delegata le richieste ai metodi di {@link MathIntegration} per eseguire operazioni
 * come il calcolo dei divisori e del massimo comune divisore (MCD).
 */
@Log4j2
@RestController
public class GatewayController {
    private final MathIntegration mathIntegration;

    public GatewayController(MathIntegration mathIntegration) {
        this.mathIntegration = mathIntegration;
    }

    /**
     * curl -X GET "http://localhost:8081/divisors?n=60&times=2&faults=10"
     */
    @GetMapping("/divisors")
    public DivisorsWithLatency getPrimeDivisors(
            @RequestParam Long n,
            @RequestParam(defaultValue = "1000") Long times,
            @RequestParam(defaultValue = "0") Long faults) {
        return mathIntegration.getDivisors(n, times, faults);
    }

    @GetMapping("/mcd")
    public MCDWithLatency getMCD(
            @RequestParam Long a,
            @RequestParam Long b,
            @RequestParam(defaultValue = "1") Long times,
            @RequestParam(defaultValue = "0") Long faults) {
        List<Long> aDivisors = mathIntegration.getDivisors(a, times, faults).divisors();
        List<Long> bDivisors = mathIntegration.getDivisors(b, times, faults).divisors();

        return mathIntegration.getMCD(a, b, aDivisors, bDivisors, times, faults);
    }
}
