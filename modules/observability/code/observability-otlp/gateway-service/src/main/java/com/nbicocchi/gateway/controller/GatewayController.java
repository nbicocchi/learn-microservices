package com.nbicocchi.gateway.controller;

import com.nbicocchi.gateway.dto.DivisorsWithLatency;
import com.nbicocchi.gateway.service.MathIntegration;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class GatewayController {
    MathIntegration mathIntegration;

    public GatewayController(MathIntegration mathIntegration) {
        this.mathIntegration = mathIntegration;
    }

    @GetMapping("/divisors")
    public DivisorsWithLatency getPrimeDivisors(
            @RequestParam Long n,
            @RequestParam(defaultValue = "1000") Long times,
            @RequestParam(defaultValue = "0") Long faults) {
        log.trace("getPrimeDivisors({}-{})", n, times);
        return mathIntegration.getDivisors(n, times, faults);
    }
}
