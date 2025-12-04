package com.nbicocchi.product.controller;

import com.nbicocchi.product.persistence.model.Product;
import com.nbicocchi.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{uuid}")
    public Product findByUuid(@PathVariable String uuid) {
        log.info("GET /products/{} — findByUuid()", uuid);
        return productService.findByUuid(uuid)
                .orElseThrow(() -> {
                    log.warn("Product with UUID {} not found", uuid);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping
    public Iterable<Product> findAll() {
        log.info("GET /products — findAll()");
        return productService.findAll();
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        log.info("POST /products — create(), product={}", product);
        Product saved = productService.save(product);
        log.info("Product created with UUID {}", saved.getUuid());
        return saved;
    }

    @PutMapping("/{uuid}")
    public Product update(@PathVariable String uuid, @RequestBody Product product) {
        log.info("PUT /products/{} — update()", uuid);

        Optional<Product> optional = productService.findByUuid(uuid);
        optional.orElseThrow(() -> {
            log.warn("Product with UUID {} not found for update", uuid);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        product.setId(optional.get().getId());

        Product updated = productService.save(product);
        log.info("Product with UUID {} updated", uuid);
        return updated;
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable String uuid) {
        log.info("DELETE /products/{} — delete()", uuid);

        Optional<Product> optional = productService.findByUuid(uuid);
        optional.orElseThrow(() -> {
            log.warn("Product with UUID {} not found for deletion", uuid);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        productService.delete(optional.get());
        log.info("Product with UUID {} deleted", uuid);
    }
}
