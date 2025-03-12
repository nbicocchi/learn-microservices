package com.nbicocchi.math.handler;

import com.nbicocchi.math.model.Event;
import com.nbicocchi.math.model.ProxyRequest;
import com.nbicocchi.math.service.PrimeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Log4j2
@Component
public class EventReceiver {
    private final PrimeService primeService;

    public EventReceiver(PrimeService primeService) {
        this.primeService = primeService;
    }

    @Bean
    public Consumer<Event<String, ProxyRequest>> processPrimeRequest() {
        return event -> {
            List<Long> primes = primeService.computePrimes(
                    event.getData().lowerBound(),
                    event.getData().upperBound());
            log.info("Primes: {}", primes);
        };
    }
}