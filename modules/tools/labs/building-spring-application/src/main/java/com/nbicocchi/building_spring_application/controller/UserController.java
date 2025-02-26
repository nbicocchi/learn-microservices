package com.nbicocchi.building_spring_application.controller;

import com.nbicocchi.building_spring_application.model.UserModel;
import com.nbicocchi.building_spring_application.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
    public UserModel getUser(@PathVariable String email) {
        log.info("getUser() invoked");
        return userService.findUserModelByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public UserModel createUser(@RequestBody UserModel userModel) {
        log.info("createUser() invoked");
        return userService.save(userModel);
    }

    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable String email) {
        log.info("deleteUser() invoked");
        Optional<UserModel> optionalUser = userService.findUserModelByEmail(email);
        optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userService.delete(optionalUser.get());
    }
}

