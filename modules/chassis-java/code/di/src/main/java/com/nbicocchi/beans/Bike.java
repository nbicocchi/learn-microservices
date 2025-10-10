package com.nbicocchi.beans;

import org.springframework.stereotype.Component;

@Component
public class Bike implements Vehicle {
    @Override
    public void start() {
        System.out.println("Bike is starting");
    }
}

