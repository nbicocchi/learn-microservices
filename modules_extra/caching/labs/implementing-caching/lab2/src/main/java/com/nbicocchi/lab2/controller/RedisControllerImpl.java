package com.nbicocchi.lab2.controller;

import com.caching.lab2.Product;
import com.nbicocchi.lab2.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class RedisControllerImpl implements RedisController{
    @Autowired
    private RedisService redisService;

    @GetMapping
    public List<Product> getAllProducts() {
        return redisService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return redisService.getProductById(id);
    }

    @PostMapping
    public void addProduct(@RequestParam int id, @RequestParam String name) {
        Product product = new Product(id, name);
        redisService.addProduct(product);
    }

    @DeleteMapping("/{id}")
    public void removeProduct(@PathVariable int id) {
        redisService.removeProduct(id);
    }
}
