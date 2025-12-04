package com.nbicocchi.product.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AppInfoController {

    @Value("${app.name}")
    private String name;

    @Value("${app.version}")
    private String version;

    @Value("${app.maintainer}")
    private String maintainer;

    @GetMapping("/info")
    public Map<String, String> getInfo() {
        return Map.of(
                "name", name,
                "version", version,
                "maintainer", maintainer
        );
    }
}
