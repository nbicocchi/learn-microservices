package com.nbicocchi.monolith.persistence.repository;

import com.nbicocchi.monolith.persistence.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface IProductRepository extends CrudRepository<Product, Long> {

}
