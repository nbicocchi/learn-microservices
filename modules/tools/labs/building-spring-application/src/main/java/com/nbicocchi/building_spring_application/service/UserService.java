package com.nbicocchi.building_spring_application.service;


import com.nbicocchi.building_spring_application.model.UserModel;
import com.nbicocchi.building_spring_application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserModel> findUserModelByEmail(String email) {
        return userRepository.findUserModelByEmail(email);
    }

    public Iterable<UserModel> findAll() {
        return userRepository.findAll();
    }

    public UserModel save(UserModel product) {
        return userRepository.save(product);
    }

    public void delete(UserModel product) {
        userRepository.delete(product);
    }
}

