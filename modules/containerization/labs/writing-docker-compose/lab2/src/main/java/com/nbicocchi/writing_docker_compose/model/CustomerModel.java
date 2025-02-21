package com.nbicocchi.writing_docker_compose.model;

public interface CustomerModel {
    Long getCustomerId();
    String getName();
    void setName(String name);
    String getEmail();
    void setEmail(String email);
}
