package com.caching.lab3.service;

import com.caching.lab3.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private static final List<User> users = new ArrayList<>(Arrays.asList(
            new User(1, "Mario"),
            new User(2, "Luca"),
            new User(3, "Giovanni")
    ));

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(int userId) {
        return users.stream()
                .filter(product -> product.id() == userId)
                .findFirst()
                .orElse(null);
    }
}

