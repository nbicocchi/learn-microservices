package com.nbicocchi.lab2.service;

import com.caching.lab2.Product;

import java.util.List;

public interface RedisService {
    List<Product> getAllProducts();
    Product getProductById(int id);
    void addProduct(Product product);
    void removeProduct(int id);
}

