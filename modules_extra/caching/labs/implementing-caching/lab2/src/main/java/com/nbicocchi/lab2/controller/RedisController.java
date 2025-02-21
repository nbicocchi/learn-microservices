package com.nbicocchi.lab2.controller;

import com.caching.lab2.Product;
import java.util.List;

import org.springframework.web.bind.annotation.*;

public interface RedisController {
    @GetMapping
    List<Product> getAllProducts();

    @GetMapping("/{id}")
    Product getProductById(@PathVariable int id);

    @PostMapping
    void addProduct(@RequestParam int id, @RequestParam String name);

    @DeleteMapping("/{id}")
    void removeProduct(@PathVariable int id);
}


