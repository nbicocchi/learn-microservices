package com.nbicocchi.writing_docker_compose.service;

import com.nbicocchi.writing_docker_compose.model.CustomerModelImpl;

public interface CustomerService {
    CustomerModelImpl getCustomerData(Long id);
}

