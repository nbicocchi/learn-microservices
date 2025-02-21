package com.nbicocchi.lab1.service;

public interface RedisService {
    void setValue(String key, String value);
    String getValue(String key);
}

