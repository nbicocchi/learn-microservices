package com.nbicocchi.echo.controller;

import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Log
@RestController
public class EchoController {
    @PostMapping(value = "/echo")
    public Map<String, Object> echo(@RequestBody String message) {
        log.info(message);
        return Map.of("echoed_data", message);
    }

    @GetMapping(value = "/logs")
    public Map<String, Object> logs() throws IOException {
        log.info("requested_logs");
        Path path = FileSystems.getDefault().getPath("/tmp", "application.log");
        return Map.of("lines", Files.readAllLines(path));
    }
}