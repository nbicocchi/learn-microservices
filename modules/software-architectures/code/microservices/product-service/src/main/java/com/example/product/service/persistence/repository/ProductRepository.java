package com.example.product.service.persistence.repository;

import com.example.product.service.persistence.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}

