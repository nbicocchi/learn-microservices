package com.antonio.core.product.web.controller;

import com.antonio.core.product.mapper.ProductMapper;
import com.antonio.core.product.web.dto.*;
import com.antonio.core.product.web.services.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import com.antonio.core.product.web.exceptions.InvalidInputException;
import com.antonio.core.product.web.exceptions.NotFoundException;
import com.antonio.core.product.persistence.ProductEntity;
import com.antonio.core.product.persistence.ProductRepository;
import com.antonio.core.product.web.errors.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductControllerImpl implements ProductController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final ProductRepository repository;

    private final ProductMapper mapper;

    private ProductCompositeIntegration integration;


    @Autowired
    public ProductControllerImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil,
                                 ProductCompositeIntegration integration) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    // Used to read a product by productId
    @Override
    public Product getProduct(@Argument int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        ProductEntity entity = repository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));

        Product response = mapper.entityToApi(entity);

        LOG.debug("getProduct: found productId: {}", response.getProductId());

        return response;
    }

    // Used to create a product
    @Override
    public Product createProduct(@Argument Product input) {
        try {
            ProductEntity entity = mapper.apiToEntity(input);
            ProductEntity newEntity = repository.save(entity);

            LOG.debug("createProduct: entity created for productId: {}", input.getProductId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + input.getProductId());
        }
    }

    // Used to delete a product by productId
    @Override
    public Boolean deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        if (repository.findByProductId(productId).isEmpty()) {
            throw new NotFoundException("No product found for productId: " + productId);
        }
        repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
        return Boolean.TRUE;
    }

    // Used to get a product aggregate by productId (product and reviews)
    @Override
    public ProductAggregate getProductAggregate(@Argument int productId) {

        LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);

        Product product = getProduct(productId);

        List<Review> reviews = integration.getReviews(productId);

        LOG.debug("getCompositeProduct: aggregate entity found for productId: {}", productId);

        return parsingProductAggregate(product, reviews);
    }

    // Create ProductAggregate from the product and reviews to show them all together
    private ProductAggregate parsingProductAggregate(
            Product product,
            List<Review>reviews) {

        // 1. Setup product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        // 2. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
                reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        return new ProductAggregate(productId, name, weight, reviewSummaries);
    }

    // Used to create a product aggregate (product and reviews)
    public ProductAggregate createProductAggregate(@Argument ProductAggregateInput input) {
        createProduct(new Product(input.getProductId(), input.getName(), input.getWeight()));
        integration.createReview(new Review(input.getProductId(), input.getReviewId(), input.getAuthor(), input.getSubject(), input.getContent()));
        List<ReviewSummary> summary = integration.getReviews(input.getProductId()).stream()
                .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                .collect(Collectors.toList());
        return new ProductAggregate(input.getProductId(), input.getName(), input.getWeight(), summary);

    }

    // Used to delete a product aggregate by productId (product and reviews aka purging)
    public Boolean deleteProductAggregate(@Argument int productId) {
        deleteProduct(productId);
        integration.deleteReviews(productId);
        return Boolean.TRUE;
    }
}