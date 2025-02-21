package com.nbicocchi.events.events;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.random.RandomGenerator;

@Component
public class ScheduledTask {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private final MessageSender messageSender;

    public ScheduledTask(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Scheduled(fixedRate = 1000)
    public void randomMessage() {
        int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
        Event<String, Double> event = new Event<>(
                Event.Type.class.getEnumConstants()[index],
                UUID.randomUUID().toString(),
                RANDOM.nextDouble(100)
        );
        messageSender.sendMessage("mathRequest-out-0", event);
    }
}
