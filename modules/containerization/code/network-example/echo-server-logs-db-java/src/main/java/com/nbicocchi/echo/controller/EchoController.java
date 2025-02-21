package com.nbicocchi.echo.controller;

import com.nbicocchi.echo.persistence.model.LogLine;
import com.nbicocchi.echo.persistence.repository.LogLineRepository;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Log
@RestController
public class EchoController {
    LogLineRepository logLineRepository;

    public EchoController(LogLineRepository logLineRepository) {
        this.logLineRepository = logLineRepository;
    }

    @PostMapping(value = "/echo")
    public Map<String, Object> echo(@RequestBody String message) {
        log.info(message);
        logLineRepository.save(new LogLine(LocalDateTime.now(), message));
        return Map.of("echoed_data", message);
    }

    @GetMapping(value = "/logs")
    public Map<String, Object> logs() {
        log.info("requested_logs");
        logLineRepository.save(new LogLine(LocalDateTime.now(), "requested_logs"));
        return Map.of("lines", logLineRepository.findAll());
    }
}