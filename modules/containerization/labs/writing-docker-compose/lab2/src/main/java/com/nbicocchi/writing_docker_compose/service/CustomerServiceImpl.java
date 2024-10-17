package com.nbicocchi.writing_docker_compose.service;


import com.nbicocchi.writing_docker_compose.model.CustomerModelImpl;
import com.nbicocchi.writing_docker_compose.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService{
    @Autowired
    private CustomerRepository customerRepository;

    @PostConstruct
    public void init() {
        System.out.println("adding customer to db");
        CustomerModelImpl customer = new CustomerModelImpl("Mario Rossi", "mariorossi@example.com");
        customerRepository.save(customer);
    }

    public CustomerModelImpl getCustomerData(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

}

