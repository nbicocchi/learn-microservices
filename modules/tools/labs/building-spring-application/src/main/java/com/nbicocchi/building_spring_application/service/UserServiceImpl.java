package com.nbicocchi.building_spring_application.service;


import com.nbicocchi.building_spring_application.model.UserModelImpl;
import com.nbicocchi.building_spring_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public UserModelImpl getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserModelImpl createUser(UserModelImpl userModelImpl) {
        return userRepository.save(userModelImpl);
    }

    public UserModelImpl updateUser(Long id, UserModelImpl userModelImplDetails) {
        UserModelImpl userModelImpl = userRepository.findById(id).orElse(null);
        if (userModelImpl != null) {
            userModelImpl.setName(userModelImplDetails.getName());
            userModelImpl.setEmail(userModelImplDetails.getEmail());
            return userRepository.save(userModelImpl);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

