package com.nbicocchi.building_spring_application.controller;

import com.nbicocchi.building_spring_application.model.UserModelImpl;
import com.nbicocchi.building_spring_application.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserControllerImpl implements UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserControllerImpl.class);

    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping("/{id}")
    public UserModelImpl getUser(@PathVariable Long id) {
        LOG.info("getUserById() invoked");
        return userServiceImpl.getUser(id);
    }

    @PostMapping
    public UserModelImpl createUser(@RequestBody UserModelImpl userModelImpl) {
        LOG.info("createUser() invoked");
        return userServiceImpl.createUser(userModelImpl);
    }

    @PutMapping("/{id}")
    public UserModelImpl updateUser(@PathVariable Long id, @RequestBody UserModelImpl userModelImplDetails) {
        LOG.info("updateUser() invoked");
        return userServiceImpl.updateUser(id, userModelImplDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        LOG.info("deleteUser() invoked");
        userServiceImpl.deleteUser(id);
    }
}

