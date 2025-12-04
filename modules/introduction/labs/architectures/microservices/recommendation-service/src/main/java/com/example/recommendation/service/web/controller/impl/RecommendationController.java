package com.example.recommendation.service.web.controller.impl;

import com.example.recommendation.service.service.IRecommendationService;
import com.example.recommendation.service.web.controller.IRecommendationController;
import com.example.recommendation.service.web.dto.RecommendationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class RecommendationController implements IRecommendationController {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationController.class);

    private final IRecommendationService recommendationService;

    @Autowired
    public RecommendationController(IRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    public RecommendationDTO createRecommendation( @RequestBody RecommendationDTO recommendation){
        LOG.debug("createRecommendation: Creates the recommendation with ID: {}", recommendation.recommendationId());
        RecommendationDTO r = recommendationService.save(recommendation);
        LOG.debug("createRecommendation: recommendations created with ID: {}", recommendation.recommendationId());
        return r;
    }

    @Override
    public void deleteRecommendation( @PathVariable Long productId, @PathVariable Long recommendationId){
        LOG.debug("deleteRecommendation: Deletes the recommendation with ID: {}", recommendationId);
        recommendationService.deleteById(productId, recommendationId);
        LOG.debug("deleteRecommendation: recommendations deleted for ID: {}", recommendationId);
    }

    @Override
    public List<RecommendationDTO> getRecommendations(@PathVariable Long productId) {
        return recommendationService.findRecommendationsByProductId(productId);
    }

    @Override
    public void deleteAllRecommendations(@PathVariable Long productId){
        LOG.debug("deleteAllReviews: Deletes the review with ID: {}", productId);
        recommendationService.deleteRecommendations(productId);
        LOG.debug("deleteAllReviews: review deleted for ID: {}", productId);
    }

}
