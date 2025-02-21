package com.nbicocchi.writing_docker_compose.repository;

import com.nbicocchi.writing_docker_compose.model.CustomerModelImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModelImpl, Long> {

}
