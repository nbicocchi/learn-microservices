package com.nbicocchi.writing_docker_compose.controller;

import com.nbicocchi.writing_docker_compose.model.CustomerModelImpl;
import com.nbicocchi.writing_docker_compose.service.CustomerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CustomerControllerImpl implements CustomerController{
    private static final Logger LOG = LoggerFactory.getLogger(CustomerControllerImpl.class);

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @GetMapping("/{id}")
    public CustomerModelImpl getCustomerData(@PathVariable Long id) {
        LOG.info("getCustomerData() invoked");
        return customerServiceImpl.getCustomerData(id);
    }

}

