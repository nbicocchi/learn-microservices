package com.nbicocchi.product;

import com.nbicocchi.product.persistence.model.Product;
import com.nbicocchi.product.persistence.repository.ProductRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements ApplicationRunner {
    ProductRepository productRepository;

    public App(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        productRepository.save(new Product("Laptop", 2.2));
        productRepository.save(new Product("Bike", 5.5));
        productRepository.save(new Product("Shirt", 0.2));
    }
}
