package com.nbicocchi.monolith.adapters.service;

import com.nbicocchi.monolith.adapters.persistence.implementation.Product;
import com.nbicocchi.monolith.adapters.persistence.implementation.Recommendation;
import com.nbicocchi.monolith.adapters.persistence.implementation.Review;
import com.nbicocchi.monolith.adapters.persistence.repository.IProductCompositeRepository;
import com.nbicocchi.monolith.adapters.api.mapper.ProductAggregateMapper;
import com.nbicocchi.monolith.adapters.api.mapper.RecommendationMapper;
import com.nbicocchi.monolith.adapters.api.mapper.ReviewMapper;
import com.nbicocchi.monolith.core.entity.product.IProductEntity;
import com.nbicocchi.monolith.core.entity.recommendation.IRecommendationEntity;
import com.nbicocchi.monolith.core.entity.review.IReviewEntity;
import com.nbicocchi.monolith.core.usecase.creation.CreationOutputBoundary;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CreationService implements CreationOutputBoundary {

    private final IProductCompositeRepository productRepository;
    private final ProductAggregateMapper productMapper;
    private final RecommendationMapper recommendationMapper;
    private final ReviewMapper reviewMapper;

    public CreationService(IProductCompositeRepository productRepository, ProductAggregateMapper productMapper, RecommendationMapper recommendationMapper, ReviewMapper reviewMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.recommendationMapper = recommendationMapper;
        this.reviewMapper = reviewMapper;
    }


    @Override
    public IProductEntity saveProduct(IProductEntity product) {
        Product p = productRepository.save(productMapper.entityToPersistence(product));
        return productMapper.persistenceToEntity(p);
    }

    @Override
    public IRecommendationEntity saveRecommendation(IRecommendationEntity recommendation) {
        Optional<Product> p = productRepository.findById(recommendation.getProductId());
        if(p.isPresent()){
            Product product = p.get();
            Recommendation r = recommendationMapper.entityToPersistence(recommendation);
            product.addRecommendation(r);
            productRepository.save(product);
            return recommendationMapper.persistenceToEntity(r);
        }else throw new NoSuchElementException();
    }

    @Override
    public IReviewEntity saveReview(IReviewEntity review){
        Optional<Product> p = productRepository.findById(review.getProductId());
        if(p.isPresent()){
            Product product = p.get();
            Review r = reviewMapper.entityToPersistence(review);
            product.addReview(r);
            productRepository.save(product);
            return reviewMapper.persistenceToEntity(r);
        }else throw new NoSuchElementException();
    }




}
