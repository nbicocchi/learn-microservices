package com.larcangeli.monolith.product.web.controllers;

import com.larcangeli.monolith.product.shared.ProductDTO;
import com.larcangeli.monolith.product.shared.IProductService;
import com.larcangeli.monolith.recommendation.shared.IRecommendationService;
import com.larcangeli.monolith.recommendation.shared.RecommendationDTO;
import com.larcangeli.monolith.review.shared.IReviewService;
import com.larcangeli.monolith.review.shared.ReviewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
class ProductController implements IProductController{

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final IProductService productService;
    private final IRecommendationService recommendationService;
    private final IReviewService reviewService;
    private final ApplicationEventPublisher events;

    @Autowired
    public ProductController(IProductService productService, IRecommendationService recommendationService, IReviewService reviewService, ApplicationEventPublisher events) {
        this.productService = productService;
        this.recommendationService = recommendationService;
        this.reviewService = reviewService;
        this.events = events;
    }


    @Override
    public ProductDTO getProduct(@PathVariable Long productId){
        LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);
        ProductDTO p = productService.findById(productId);

        List<RecommendationDTO> recommendations = recommendationService.findRecommendationsByProductId(productId);
        List<ReviewDTO> reviews = reviewService.findReviewsByProductId(productId);

        return createProductAggregate(p,recommendations,reviews);
    }

    @Override
    public List<ProductDTO> getAllProducts(){
        return productService.findAll().stream().map(p -> getProduct(p.productId())).collect(Collectors.toList());
    }

    @Override
    public ProductDTO createProduct(@RequestBody ProductDTO body){
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.productId());
            ProductDTO p = productService.save(body);
            if(!body.recommendations().isEmpty()){
                body.recommendations().forEach(r -> {
                    RecommendationDTO rec = new RecommendationDTO(r.recommendationId(),p.productId(),r.version(),r.author(),r.rating(),r.content());
                    events.publishEvent(rec);
                });
            }
            if(!body.reviews().isEmpty()){
                body.reviews().forEach(r -> {
                    ReviewDTO rev = new ReviewDTO(r.reviewId(),p.productId(),r.author(),r.subject(),r.content());
                    events.publishEvent(rev);
                });
            }
            LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.productId());
            return p;


    }

    @Override
    public void deleteProduct(@PathVariable Long productId){
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
        productService.deleteById(productId);
        events.publishEvent(productId);
        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);

    }

    private ProductDTO createProductAggregate(
            ProductDTO p,
            List<RecommendationDTO> recommendations,
            List<ReviewDTO> reviews){

        // 1. Setup product info
        Long productId = p.productId();
        String name = p.name();
        int weight = p.weight();
        Integer version = p.version();

        // 2. Copy summary recommendation info, if available
        return new ProductDTO(productId, version, name, weight, recommendations, reviews);
    }
}

