package com.example.product.service.web.controller;

import com.example.product.service.web.dto.ProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

public interface IProductController {
    @GetMapping(value = "/products/{productId}", produces = "application/json")
    ProductDTO getProduct(@PathVariable Long productId);

    @GetMapping(value = "/products", produces = "application/json")
    List<ProductDTO> getAllProducts();

    @PostMapping(value    = "/products", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ProductDTO createProduct(@RequestBody ProductDTO body);

    @DeleteMapping(value = "/products/{productId}")
    void deleteProduct(@PathVariable Long productId);
}
