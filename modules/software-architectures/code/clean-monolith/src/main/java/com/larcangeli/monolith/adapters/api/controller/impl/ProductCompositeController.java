package com.larcangeli.monolith.adapters.api.controller.impl;

import com.larcangeli.monolith.adapters.api.controller.IProductCompositeController;
import com.larcangeli.monolith.adapters.api.dto.ProductAggregateDTO;
import com.larcangeli.monolith.adapters.api.dto.RecommendationDTO;
import com.larcangeli.monolith.adapters.api.dto.ReviewDTO;
import com.larcangeli.monolith.adapters.api.util.exceptions.InvalidInputException;
import com.larcangeli.monolith.adapters.api.util.exceptions.UnprocessableEntityException;
import com.larcangeli.monolith.core.usecase.creation.CreationInputBoundary;
import com.larcangeli.monolith.core.usecase.removal.RemovalInputBoundary;
import com.larcangeli.monolith.core.usecase.retrieval.RetrievalInputBoundary;
import com.larcangeli.monolith.adapters.api.mapper.ProductAggregateMapper;
import com.larcangeli.monolith.adapters.api.mapper.RecommendationMapper;
import com.larcangeli.monolith.adapters.api.mapper.ReviewMapper;
import com.larcangeli.monolith.adapters.api.util.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ProductCompositeController implements IProductCompositeController{

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeController.class);

    private final CreationInputBoundary creationInputBoundary;
    private final RemovalInputBoundary removalInputBoundary;
    private final RetrievalInputBoundary retrievalInputBoundary;
    private final ProductAggregateMapper productMapper;
    private final RecommendationMapper recommendationMapper;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ProductCompositeController(CreationInputBoundary creationInputBoundary, RemovalInputBoundary removalInputBoundary, RetrievalInputBoundary retrievalInputBoundary, ProductAggregateMapper productMapper, RecommendationMapper recommendationMapper, ReviewMapper reviewMapper) {
        this.creationInputBoundary = creationInputBoundary;
        this.removalInputBoundary = removalInputBoundary;
        this.retrievalInputBoundary = retrievalInputBoundary;
        this.productMapper = productMapper;
        this.recommendationMapper = recommendationMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public ProductAggregateDTO getProduct(@PathVariable Long productId){
        if(productId < 0){
            throw new UnprocessableEntityException("Invalid productId: " + productId);
        }
        LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);
        try{
            return productMapper.entityToDto(retrievalInputBoundary.getProduct(productId));

        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }

    @Override
    public List<ProductAggregateDTO> getAllProducts(){
        return retrievalInputBoundary.getAllProducts().stream().map(productMapper::entityToDto).toList();
    }

    @Override
    public ProductAggregateDTO createProduct(@RequestBody ProductAggregateDTO request){
        try{
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", request.productId());
            return productMapper.entityToDto(creationInputBoundary.createProduct(productMapper.dtoToEntity(request)));

        }catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public void deleteProduct(@PathVariable Long productId){
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
        removalInputBoundary.deleteProduct(productId);

        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);

    }

    @Override
    public RecommendationDTO createRecommendation(@PathVariable Long productId, @RequestBody RecommendationDTO recommendation){
        return recommendationMapper.entityToDto(creationInputBoundary.createRecommendation(recommendationMapper.dtoToEntity(recommendation)));
    }

    @Override
    public void deleteRecommendation(@PathVariable Long productId, @PathVariable Long recommendationId){
        LOG.debug("deleteCompositeProduct: Deletes the recommendation with ID: {}", recommendationId);

        try{
            removalInputBoundary.deleteRecommendation(productId, recommendationId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Recommendation with ID: " + recommendationId + " not found");
        }
        LOG.debug("deleteCompositeProduct: recommendations deleted for ID: {}", recommendationId);

    }

    @Override
    public ReviewDTO createReview(@PathVariable Long productId, @RequestBody ReviewDTO review){
        return reviewMapper.entityToDto(creationInputBoundary.createReview(reviewMapper.dtoToEntity(review)));
    }

    @Override
    public void deleteReview(@PathVariable Long productId, @PathVariable Long reviewId){
        LOG.debug("deleteCompositeProduct: Deletes the review with ID: {}", reviewId);

        try{
            removalInputBoundary.deleteReview(productId, reviewId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Recommendation with ID: " + reviewId + " not found");
        }
        LOG.debug("deleteCompositeProduct: recommendations deleted for ID: {}", reviewId);

    }


}
