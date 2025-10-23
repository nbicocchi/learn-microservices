package com.nbicocchi.beans;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppService {
    private final String appName;
    private final String appVersion;
    private final Integer maxUsers;

    public AppService(
            @Value("${app.name}") String appName,
            @Value("${app.version}") String appVersion,
            @Value("${app.max-users}") Integer maxUsers) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.maxUsers = maxUsers;
    }

    @PostConstruct
    public void printAppDetails() {
        System.out.println("Application Name: " + appName);
        System.out.println("Version: " + appVersion);
        System.out.println("Max Users: " + maxUsers);
    }
}
