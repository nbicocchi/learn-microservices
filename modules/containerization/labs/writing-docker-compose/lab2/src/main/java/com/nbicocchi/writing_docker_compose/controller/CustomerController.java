package com.nbicocchi.writing_docker_compose.controller;


import com.nbicocchi.writing_docker_compose.model.CustomerModelImpl;

public interface CustomerController {
    CustomerModelImpl getCustomerData(Long id);
}
