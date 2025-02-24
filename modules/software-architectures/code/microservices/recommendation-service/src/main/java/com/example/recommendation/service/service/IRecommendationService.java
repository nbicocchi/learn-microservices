package com.example.recommendation.service.service;

import com.example.recommendation.service.web.dto.RecommendationDTO;
import org.springframework.context.event.EventListener;

import java.util.List;

public interface IRecommendationService {

    RecommendationDTO save(RecommendationDTO recommendation);

    @EventListener
    void saveRecommendationOnProductCreation(RecommendationDTO recommendation);

    void deleteById(Long productId, Long recommendationId);

    @EventListener
    void deleteRecommendations(Long productId);

    List<RecommendationDTO> findRecommendationsByProductId(Long productId);
}
