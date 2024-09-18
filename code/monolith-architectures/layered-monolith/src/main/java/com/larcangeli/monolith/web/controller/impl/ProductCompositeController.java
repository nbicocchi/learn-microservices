package com.larcangeli.monolith.web.controller.impl;

import com.larcangeli.monolith.mapper.ProductAggregateMapper;
import com.larcangeli.monolith.mapper.RecommendationMapper;
import com.larcangeli.monolith.mapper.ReviewMapper;
import com.larcangeli.monolith.persistence.model.Product;
import com.larcangeli.monolith.persistence.model.Recommendation;
import com.larcangeli.monolith.persistence.model.Review;
import com.larcangeli.monolith.service.IProductCompositeService;
import com.larcangeli.monolith.web.controller.IProductCompositeController;
import com.larcangeli.monolith.web.dto.ProductAggregateDTO;
import com.larcangeli.monolith.web.dto.RecommendationDTO;
import com.larcangeli.monolith.web.dto.ReviewDTO;
import com.larcangeli.monolith.web.exceptions.InvalidInputException;
import com.larcangeli.monolith.web.exceptions.NotFoundException;
import com.larcangeli.monolith.web.exceptions.UnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ProductCompositeController implements IProductCompositeController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeController.class);

    private final ProductAggregateMapper productMapper;
    private final RecommendationMapper recommendationMapper;
    private final ReviewMapper reviewMapper;
    private final IProductCompositeService productService;

    @Autowired
    public ProductCompositeController(ProductAggregateMapper productMapper, RecommendationMapper recommendationMapper, ReviewMapper reviewMapper, IProductCompositeService productService) {
        this.productMapper = productMapper;
        this.recommendationMapper = recommendationMapper;
        this.reviewMapper = reviewMapper;
        this.productService = productService;
    }

    @Override
    public ProductAggregateDTO getProduct(@PathVariable Long productId){
        if(productId < 0)
            throw new UnprocessableEntityException("Invalid productId: " + productId);
        LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);
        Optional<Product> p = productService.findById(productId);
        if(p.isEmpty()){
            throw new NotFoundException("No product found with ID: " + productId);
        }

        Set<Recommendation>     recommendations = productService.findRecommendationsByProductId(p.get().getProductId());
        Set<Review>             reviews = productService.findReviewsByProductId(p.get().getProductId());

        return createProductAggregate(p.get(), recommendations, reviews);
    }

    @Override
    public List<ProductAggregateDTO> getAllProducts(){
        Iterable<Product> allProducts = productService.findAll();
        List<ProductAggregateDTO> aggregates = new ArrayList<>();
        allProducts.forEach(p -> aggregates.add(createProductAggregate(p,
                productService.findRecommendationsByProductId(p.getProductId()),
                productService.findReviewsByProductId(p.getProductId()))));

        return aggregates;
    }

    @Override
    public ProductAggregateDTO createProduct(@RequestBody ProductAggregateDTO body){
        try{
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.productId());

            Set<Recommendation> recommendations = new HashSet<>();
            Set<Review> reviews = new HashSet<>();
            if (body.recommendations() != null) {
                recommendations = recommendationMapper.recommendationDTOsToRecommendations(body.recommendations());
            }
            if (body.reviews() != null) {
                reviews = reviewMapper.reviewDTOsToReviews(body.reviews());
            }

            Product p = new Product(recommendations, reviews, body.name(), body.weight(), body.version());

            LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.productId());
            return productMapper.productAggregateToProductAggregateDTO(productService.save(p));

        }catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public void deleteProduct(@PathVariable Long productId){
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
        productService.deleteById(productId);

        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);

    }

    @Override
    public RecommendationDTO createRecommendation(@PathVariable Long productId, @RequestBody RecommendationDTO recommendation){
        Optional<Product> p = productService.findById(productId);
        if(p.isEmpty()){
            throw new NotFoundException("No product found with ID: " + recommendation.productId());
        }
        Recommendation r = new Recommendation(productId, recommendation.author(), recommendation.rating(), recommendation.content());
        productService.saveRecommendation(r);

        return recommendationMapper.recommendationToRecommendationDTO(r);
    }

    @Override
    public void deleteRecommendation(@PathVariable Long productId, @PathVariable Long recommendationId){
        LOG.debug("deleteCompositeProduct: Deletes the recommendation with ID: {}", recommendationId);

        try{
            productService.deleteRecommendation(productId, recommendationId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Recommendation with ID: " + recommendationId + " not found");
        }
        LOG.debug("deleteCompositeProduct: recommendations deleted for ID: {}", recommendationId);

    }

    @Override
    public ReviewDTO createReview(@PathVariable Long productId, @RequestBody ReviewDTO review){
        Optional<Product> p = productService.findById(productId);
        if(p.isEmpty()){
            throw new NotFoundException("No product found with ID: " + review.productId());
        }
        Review r = new Review(productId, review.author(), review.subject(), review.content());
        productService.saveReview(r);

        return reviewMapper.reviewToReviewDTO(r);
    }

    @Override
    public void deleteReview(@PathVariable Long productId, @PathVariable Long reviewId){
        LOG.debug("deleteCompositeProduct: Deletes the review with ID: {}", reviewId);

        try{
            productService.deleteReview(productId, reviewId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Recommendation with ID: " + reviewId + " not found");
        }
        LOG.debug("deleteCompositeProduct: recommendations deleted for ID: {}", reviewId);

    }

    private ProductAggregateDTO createProductAggregate(
            Product p,
            Set<Recommendation> recommendations,
            Set<Review> reviews){

        // 1. Setup product info
        Long productId = p.getProductId();
        String name = p.getName();
        int weight = p.getWeight();
        Integer version = p.getVersion();

        // 2. Copy summary recommendation info, if available
        List<RecommendationDTO> recommendationDTOs = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationDTO(r.getRecommendationId(), r.getProductId(), r.getVersion(), r.getAuthor(), r.getRating(), r.getContent()))
                        .toList();

        // 3. Copy summary review info, if available
        List<ReviewDTO> reviewDTOs = (reviews == null) ? null :
                reviews.stream()
                        .map(r -> new ReviewDTO(r.getReviewId(), r.getProductId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .toList();

        return new ProductAggregateDTO(productId, version, name, weight, recommendationDTOs, reviewDTOs);
    }
}
