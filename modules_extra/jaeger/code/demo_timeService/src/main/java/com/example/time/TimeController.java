package com.example.time;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import io.opentelemetry.api.trace.Tracer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@RestController
public class TimeController {

    private static final Logger logger = LoggerFactory.getLogger(TimeController.class);

    @Autowired
    private Tracer tracer;

    @GetMapping("/time")
    public String getCurrentTime(@RequestParam(required = false, defaultValue = "0") int delay,
                                 @RequestParam(required = false, defaultValue = "0") int faultPercent) {
        Span span = tracer.spanBuilder("getCurrentTime").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("Received request for current time with delay: {} seconds and faultPercent: {}", delay, faultPercent);

            if (delay > 0) {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (faultPercent > 0 && new Random().nextInt(100) < faultPercent) {
                String errorMessage = "An error occurred while fetching the current time.";
                logger.error(errorMessage);
                return errorMessage;
            }

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = now.format(formatter);
            String responseMessage = "L'ora corrente è " + formattedTime;
            logger.info("Response: {}", responseMessage);
            return responseMessage;
        } finally {
            span.end();
        }
    }
}