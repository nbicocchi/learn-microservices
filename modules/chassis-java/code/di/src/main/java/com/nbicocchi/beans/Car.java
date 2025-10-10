package com.nbicocchi.beans;

import org.springframework.stereotype.Component;

@Component
public class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car is starting");
    }
}

