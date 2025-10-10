package com.nbicocchi.monolith.adapters.persistence.repository;

import com.nbicocchi.monolith.adapters.persistence.implementation.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IProductRepository extends CrudRepository<Product, Long> {

    List<Product> findAll();

}
