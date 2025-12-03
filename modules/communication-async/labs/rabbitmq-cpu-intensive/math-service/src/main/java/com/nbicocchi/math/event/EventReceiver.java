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
    EventSender eventSender;
    private final MathService mathService;

    @Bean
    public Consumer<Event<String, EventMathRequest>> processPrimes() {
        return event -> {
            List<Long> divisors = null;
            for (int i = 0; i < event.getData().times(); i++) {
                divisors = mathService.findPrimeDivisors(event.getData().number());
            }
            log.info("Divisors: {}", divisors);

            Event<String, EventNotification> outputEvent = new Event<>(UUID.randomUUID().toString(), new EventNotification(divisors.toString()));
            eventSender.send("processPrimes-out-0", outputEvent,
                    new EventSender.Header<>("routingKey", "notification.divisors"));
        };
    }
}