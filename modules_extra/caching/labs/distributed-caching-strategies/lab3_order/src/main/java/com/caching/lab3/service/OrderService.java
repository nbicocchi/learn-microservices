package com.caching.lab3.service;

import com.caching.lab3.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    private final RestTemplate restTemplate;
    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(String userId) {
        String url = "http://localhost:8080/users/" + userId;
        return restTemplate.getForObject(url, User.class);
    }
}

