package com.nbicocchi.monolith.core.entity.recommendation.impl;

import com.nbicocchi.monolith.core.entity.recommendation.IRecommendationFactory;
import com.nbicocchi.monolith.core.entity.recommendation.IRecommendationEntity;
import org.springframework.stereotype.Component;

@Component
public class RecommendationFactory implements IRecommendationFactory {
    @Override
    public IRecommendationEntity createRecommendation(Integer version, String author, int rating, String content) {
        return new RecommendationEntity(version,author,rating,content);
    }
    @Override
    public IRecommendationEntity createRecommendation(Long productId, Integer version, String author, int rating, String content) {
        return new RecommendationEntity(productId,version,author,rating,content);
    }
    @Override
    public IRecommendationEntity createRecommendation(Long recommendationId, Long productId, Integer version, String author, int rating, String content) {
        return new RecommendationEntity(recommendationId,productId,version,author,rating,content);
    }
}
