package com.nbicocchi.math.event;

import com.nbicocchi.math.service.MathService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Log4j2
@Component
@AllArgsConstructor
public class EventReceiver {
    private final MathService mathService;

    @Bean
    public Consumer<Event<String, EventMathRequest>> processPrimeRequest() {
        return event -> {
            List<Long> divisors = null;
            for (int i = 0; i < event.getData().times(); i++) {
                divisors = mathService.findPrimeDivisors(event.getData().number());
            }
            log.info("Divisors: {}", divisors);
        };
    }
}