package com.nbicocchi.lab2.service;

import com.caching.lab2.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {
    private static final List<Product> products = new ArrayList<>(Arrays.asList(
            new Product(1, "Product A"),
            new Product(2, "Product B"),
            new Product(3, "Product C")
    ));

    @Cacheable("products")
    public List<Product> getAllProducts() {
        return products;
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(int id) {
        return products.stream()
                .filter(product -> product.id() == id)
                .findFirst()
                .orElse(null);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void addProduct(Product product) {
        products.add(product);
    }

    @CacheEvict(value = "product", key = "#id")
    public void removeProduct(int id) {
        products.removeIf(product -> product.id() == id);
    }
}

