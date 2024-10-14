package com.nbicocchi.product;

import com.nbicocchi.product.persistence.model.Product;
import com.nbicocchi.product.persistence.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class App implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
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

        Iterable<Product> products = productRepository.findAll();
        for (Product product : products) {
            LOG.info(product.toString());
        }
    }
}
