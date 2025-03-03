package com.nbicocchi.building_spring_application.controller;

import com.nbicocchi.building_spring_application.model.UserModel;
import com.nbicocchi.building_spring_application.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
public class UserController {
    UserService userService;
    String appName;
    String appVersion;

    public UserController(UserService userService,
                          @Value("${app.name}") String appName,
                          @Value("${app.version}") String appVersion) {
        this.userService = userService;
        this.appName = appName;
        this.appVersion = appVersion;
    }

    @GetMapping("/greet")
    public Map<String, String> greet() {
        log.debug("greet() invoked");
        return Map.of(
                "Application name", appName,
                "Application version", appVersion);
    }

    @GetMapping("/sleep")
    public void sleep() throws InterruptedException {
        log.debug("sleep() invoked");
        Thread.sleep(100);
    }

    @GetMapping("/users")
    public Iterable<UserModel> findAll() {
        log.debug("findAll() invoked");
        return userService.findAll();
    }

    @GetMapping("/users/{email}")
    public UserModel findUserModelByEmail(@PathVariable String email) {
        log.debug("findUserModelByEmail() invoked");
        return userService.findUserModelByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/users")
    public UserModel save(@RequestBody UserModel userModel) {
        log.debug("save() invoked");
        return userService.save(userModel);
    }

    @PutMapping("/users/{email}")
    public void update(@PathVariable String email, @RequestBody UserModel userModel) {
        log.debug("update() invoked");
        Optional<UserModel> optionalUser = userService.findUserModelByEmail(email);
        optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userModel.setId(optionalUser.get().getId());
        userService.save(userModel);
    }

    @DeleteMapping("/users/{email}")
    public void delete(@PathVariable String email) {
        log.debug("delete() invoked");
        Optional<UserModel> optionalUser = userService.findUserModelByEmail(email);
        optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userService.delete(optionalUser.get());
    }
}

