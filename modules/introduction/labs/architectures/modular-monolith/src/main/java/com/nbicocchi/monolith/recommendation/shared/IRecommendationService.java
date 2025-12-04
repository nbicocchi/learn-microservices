package com.nbicocchi.monolith.recommendation.shared;

import org.springframework.context.event.EventListener;

import java.util.List;

public interface IRecommendationService {

    RecommendationDTO save(RecommendationDTO recommendation);

    @EventListener
    void saveRecommendationOnProductCreation(RecommendationDTO recommendation);

    void deleteById(Long recommendationId);

    @EventListener
    void deleteRecommendations(Long productId);

    List<RecommendationDTO> findRecommendationsByProductId(Long productId);
}
