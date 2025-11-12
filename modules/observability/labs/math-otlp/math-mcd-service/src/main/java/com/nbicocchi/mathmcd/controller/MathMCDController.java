package com.nbicocchi.mathmcd.controller;

import com.nbicocchi.mathmcd.dto.MCDWithLatency;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.*;

/**
 * Controller REST per la logica di calcolo del massimo comune divisore (MCD).
 * Riceve le richieste da un gateway e calcola l’MCD tra due insiemi di divisori.
 */
@Log4j2
@RestController
public class MathMCDController {

    /**
     * Calcola il massimo comune divisore (MCD) tra due insiemi di divisori primi già calcolati.
     * Simula latenza eseguendo il calcolo più volte, e può simulare errori casuali.
     *
     * @param a         primo numero originale
     * @param b         secondo numero originale
     * @param aDivisors lista dei fattori primi del primo numero
     * @param bDivisors lista dei fattori primi del secondo numero
     * @param times     quante volte ripetere il calcolo per simulare carico (default: 1)
     * @param faults    probabilità (%) di errore da simulare (default: 0)
     * @return oggetto {@link MCDWithLatency} contenente l’MCD calcolato e la latenza
     * @throws HttpServerErrorException se simulazione errore attivata
     */
    @GetMapping("/mcd")
    public MCDWithLatency getMCD(
            @RequestParam Long a,
            @RequestParam Long b,
            @RequestParam List<Long> aDivisors,
            @RequestParam List<Long> bDivisors,
            @RequestParam(defaultValue = "1") Long times,
            @RequestParam(defaultValue = "0") Long faults) {
        log.trace("getMCD({} vs {})", a, b);

        Long begin = System.currentTimeMillis();
        Long mcd = 1L;

        Long t = times/2;
        // 50% chance di rallentamento
        if (Math.random() < 0.5) {
            t *= 100; // simula carico pesante
        }

        for (int i = 0; i < t; i++) {
            mcd = calculateMCD(aDivisors, bDivisors);
        }

        Long end = System.currentTimeMillis();

        throwErrorIfBadLuck(faults);

        return new MCDWithLatency(a, b, aDivisors, bDivisors, mcd, end - begin);
    }

    /**
     * Calcola l’MCD corretto tra due insiemi di fattori primi,
     * tenendo conto della molteplicità di ciascun fattore.
     *
     * @param aFactors lista dei fattori primi del primo numero
     * @param bFactors lista dei fattori primi del secondo numero
     * @return massimo comune divisore (MCD)
     */
    private Long calculateMCD(List<Long> aFactors, List<Long> bFactors) {
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

    /**
     * Conta la frequenza dei fattori primi in una lista.
     *
     * @param factors lista di fattori primi
     * @return mappa dei fattori con il numero di occorrenze
     */
    private Map<Long, Long> countFactors(List<Long> factors) {
        Map<Long, Long> map = new HashMap<>();
        for (Long factor : factors) {
            map.put(factor, map.getOrDefault(factor, 0L) + 1);
        }
        return map;
    }

    /**
     * Lancia un errore di tipo 503 SERVICE_UNAVAILABLE con una probabilità specificata.
     *
     * @param faults probabilità su 100 di lanciare l’errore
     * @throws HttpServerErrorException se viene generato l’errore simulato
     */
    private void throwErrorIfBadLuck(Long faults) {
        RandomGenerator RND = RandomGenerator.getDefault();
        if (RND.nextInt(0, 100) < faults) {
            throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}