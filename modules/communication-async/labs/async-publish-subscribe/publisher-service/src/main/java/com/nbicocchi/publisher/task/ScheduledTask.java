package com.nbicocchi.publisher.task;

import com.nbicocchi.publisher.events.Event;
import com.nbicocchi.publisher.events.EventSender;
import com.nbicocchi.publisher.events.SpecificEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.random.RandomGenerator;


@Log4j2
@Component
@AllArgsConstructor
public class ScheduledTask {
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();
    private final List<String> keys = List.of("money.account.created", "money.deposit", "money.withdraw");
    EventSender eventSender;

    @Scheduled(fixedRate = 100)
    public void randomMessage() {

        Event<String, SpecificEvent> event = new Event<>(
                keys.get(randomGenerator.nextInt(keys.size())),
                new SpecificEvent("abc", randomGenerator.nextDouble(100.0)));

        eventSender.send("message-out-0", event.getKey(), event);
        log.info(event.toString());
    }
}
