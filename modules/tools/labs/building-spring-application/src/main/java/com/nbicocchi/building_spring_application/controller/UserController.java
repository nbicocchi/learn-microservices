package com.nbicocchi.building_spring_application.controller;

import com.nbicocchi.building_spring_application.model.UserModelImpl;

public interface UserController {
    UserModelImpl getUser(Long id);
    UserModelImpl createUser(UserModelImpl userModelImpl);
    UserModelImpl updateUser(Long id, UserModelImpl userModelImplDetails);
    void deleteUser(Long id);
}
