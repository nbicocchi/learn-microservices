package com.baeldung.product.web.controller;

import com.baeldung.product.web.dto.ProductDto;
import com.baeldung.product.web.exceptions.InvalidInputException;
import com.baeldung.product.web.exceptions.NotFoundException;
import com.baeldung.product.mapper.ProductMapper;
import com.baeldung.product.persistence.ProductEntity;
import com.baeldung.product.persistence.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.INFO;

@RestController
public class ProductControllerImpl implements ProductController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductControllerImpl.class);
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Autowired
    public ProductControllerImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(cacheNames = "products", key = "#productId")
    @Override
    public Mono<ProductDto> getProduct(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        return repository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log(LOG.getName(), INFO)
                .map(mapper::entityToApi);
    }

    @Override
    public Mono<ProductDto> createProduct(ProductDto body) {
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }

        ProductEntity entity = mapper.apiToEntity(body);
        return repository.save(entity)
                .log(LOG.getName(), INFO)
                .onErrorMap(e -> new InvalidInputException("Duplicate key: " + body.getProductId()))
                .map(mapper::entityToApi);
    }

    @CacheEvict(cacheNames = "products", key = "#productId")
    @Override
    public Mono<Void> deleteProduct(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return repository.findByProductId(productId)
                .log(LOG.getName(), INFO)
                .map(repository::delete)
                .flatMap(e -> e);
    }
}
