package com.nbicocchi.building_spring_application.model;

public interface UserModel {
    Long getUserId();
    String getName();
    void setName(String name);
    String getEmail();
    void setEmail(String email);
}
