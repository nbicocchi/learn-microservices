package com.nbicocchi.publisher.task;

import com.nbicocchi.publisher.events.Event;
import com.nbicocchi.publisher.events.MessageSender;
import com.nbicocchi.publisher.events.SpecificEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;


@Log4j2
@Component
@AllArgsConstructor
public class ScheduledTask {
    private final MessageSender messageSender;
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();
    private final List<String> actions = List.of(
            "money.account.created",
            "money.deposit",
            "money.withdraw");
    private final List<String> accounts = List.of(
            "BA-1001-2025",
            "BA-1002-2025",
            "BA-1003-2025",
            "BA-1004-2025",
            "BA-1005-2025",
            "BA-1006-2025",
            "BA-1007-2025",
            "BA-1008-2025",
            "BA-1009-2025",
            "BA-1010-2025"
    );

    @Scheduled(fixedRate = 100)
    public void randomMessage() {
        String account = accounts.get(randomGenerator.nextInt(accounts.size()));
        String action = actions.get(randomGenerator.nextInt(actions.size()));

        Event<String, SpecificEvent> event = new Event<>(
                account,
                new SpecificEvent(
                        account,
                        action,
                        randomGenerator.nextDouble(100.0)));

        log.info("Sending event: {}", event);

        messageSender.sendMessage(event.toString());
    }
}
