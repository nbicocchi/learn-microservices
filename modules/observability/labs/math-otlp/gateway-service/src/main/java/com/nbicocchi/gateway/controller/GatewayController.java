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

    /**
     * Costruttore del controller che inietta il componente {@link MathIntegration}.
     *
     * @param mathIntegration componente che integra i servizi MATH-SERVICE e MCD-SERVICE
     */
    public GatewayController(MathIntegration mathIntegration) {
        this.mathIntegration = mathIntegration;
    }

    /**
     * Endpoint REST per ottenere i divisori di un numero intero.
     * Effettua una chiamata al servizio MATH-SERVICE tramite {@link MathIntegration#getDivisors}.
     *
     * @param n      numero da scomporre
     * @param times  numero di ritardi da simulare (default: 1000)
     * @param faults numero di errori da simulare (default: 0)
     * @return oggetto {@link DivisorsWithLatency} con i divisori e il tempo impiegato
     */
    @GetMapping("/divisors")
    public DivisorsWithLatency getPrimeDivisors(
            @RequestParam Long n,
            @RequestParam(defaultValue = "1000") Long times,
            @RequestParam(defaultValue = "0") Long faults) {
        log.trace("getPrimeDivisors({}-{})", n, times);
        return mathIntegration.getDivisors(n, times, faults);
    }

    /**
     * Endpoint REST per ottenere il massimo comune divisore (MCD) tra due numeri.
     * Ottiene prima i divisori di entrambi i numeri e poi chiama il servizio MCD-SERVICE.
     *
     * @param a      primo numero
     * @param b      secondo numero
     * @param times  numero di ritardi da simulare (default: 1)
     * @param faults numero di errori da simulare (default: 0)
     * @return oggetto {@link MCDWithLatency} con il MCD e il tempo impiegato
     */
    @GetMapping("/mcd")
    public MCDWithLatency getMCD(
            @RequestParam Long a,
            @RequestParam Long b,
            @RequestParam(defaultValue = "1") Long times,
            @RequestParam(defaultValue = "0") Long faults) {
        log.trace("getMCD({}-{}-{}-{})", a, b, times, faults);
        List<Long> aDivisors = mathIntegration.getDivisors(a, times, faults).divisors();
        List<Long> bDivisors = mathIntegration.getDivisors(b, times, faults).divisors();

        return mathIntegration.getMCD(a, b, aDivisors, bDivisors, times, faults);
    }

}
