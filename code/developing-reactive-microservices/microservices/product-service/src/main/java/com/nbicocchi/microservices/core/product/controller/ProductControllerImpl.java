package com.nbicocchi.microservices.core.product.controller;

import com.nbicocchi.api.core.product.ProductController;
import com.nbicocchi.api.core.product.ProductDto;
import com.nbicocchi.api.exceptions.InvalidInputException;
import com.nbicocchi.api.exceptions.NotFoundException;
import com.nbicocchi.microservices.core.product.persistence.ProductEntity;
import com.nbicocchi.microservices.core.product.persistence.ProductRepository;
import com.nbicocchi.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@RestController
public class ProductControllerImpl implements ProductController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductControllerImpl.class);

    private final ServiceUtil serviceUtil;

    private final ProductRepository repository;

    private final ProductMapper mapper;

    @Autowired
    public ProductControllerImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<ProductDto> createProduct(ProductDto body) {
        ProductEntity entity = mapper.apiToEntity(body);

        return repository.save(entity).log(LOG.getName(), FINE).onErrorMap(DuplicateKeyException.class, ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId())).map(e -> mapper.entityToApi(e));
    }

    @Override
    public Mono<ProductDto> getProduct(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        LOG.info("Will get product info for id={}", productId);
        return repository.findByProductId(productId).switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId))).log(LOG.getName(), FINE).map(e -> mapper.entityToApi(e)).map(e -> {
            e.setServiceAddress(serviceUtil.getServiceAddress());
            return e;
        });
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        return repository.findByProductId(productId)
                .log(LOG.getName(), FINE)
                .map(e -> repository.delete(e))
                .flatMap(e -> e);
    }
}
