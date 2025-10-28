package com.nbicocchi.product.controller;

import com.nbicocchi.product.persistence.model.Product;
import com.nbicocchi.product.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Controller
public class ProductGraphQLController {
    private final ProductService productService;

    public ProductGraphQLController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public Product productByUuid(@Argument String uuid) {
        return productService.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @QueryMapping
    public Iterable<Product> allProducts() {
        return productService.findAll();
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput product) {
        Product p = new Product(product.getUuid(), product.getName(), product.getWeight());
        return productService.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument String uuid, @Argument ProductInput product) {
        Optional<Product> existing = productService.findByUuid(uuid);
        existing.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Product p = existing.get();
        p.setName(product.getName());
        p.setWeight(product.getWeight());
        return productService.save(p);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument String uuid) {
        Optional<Product> existing = productService.findByUuid(uuid);
        existing.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        productService.delete(existing.get());
        return true;
    }

    // GraphQL input class
    public static class ProductInput {
        private String uuid;
        private String name;
        private Double weight;

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
    }
}
