package com.nbicocchi.monolith.core.entity.recommendation;

public interface IRecommendationFactory {
    IRecommendationEntity createRecommendation(Integer version, String author, int rating, String content);
    IRecommendationEntity createRecommendation(Long productId, Integer version, String author, int rating, String content);
    IRecommendationEntity createRecommendation(Long id, Long productId, Integer version, String author, int rating, String content);


}
