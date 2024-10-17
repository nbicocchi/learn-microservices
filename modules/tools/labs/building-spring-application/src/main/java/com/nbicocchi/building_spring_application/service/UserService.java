package com.nbicocchi.building_spring_application.service;

import com.nbicocchi.building_spring_application.model.UserModelImpl;

public interface UserService {
    UserModelImpl getUser(Long id);
    UserModelImpl createUser(UserModelImpl userModelImpl);
    UserModelImpl updateUser(Long id, UserModelImpl userModelImplDetails);
    void deleteUser(Long id);
}

