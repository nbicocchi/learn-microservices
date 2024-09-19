package com.larcangeli.monolith.core.entity.recommendation;

import com.larcangeli.monolith.core.entity.recommendation.IRecommendationEntity;

public interface IRecommendationFactory {
    IRecommendationEntity createRecommendation(Integer version, String author, int rating, String content);
    IRecommendationEntity createRecommendation(Long productId, Integer version, String author, int rating, String content);
    IRecommendationEntity createRecommendation(Long id, Long productId, Integer version, String author, int rating, String content);


}
