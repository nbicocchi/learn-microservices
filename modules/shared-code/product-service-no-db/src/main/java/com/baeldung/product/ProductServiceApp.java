package com.baeldung.product;

import com.baeldung.product.persistence.model.Product;
import com.baeldung.product.persistence.repository.ProductRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApp implements ApplicationRunner {
    ProductRepository productRepository;

    public ProductServiceApp(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(ProductServiceApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        productRepository.save(new Product("Laptop", 2.2));
        productRepository.save(new Product("Bike", 5.5));
        productRepository.save(new Product("Shirt", 0.2));
    }
}
