package com.larcangeli.monolith.recommendation.web.controllers;

import com.larcangeli.monolith.recommendation.shared.RecommendationDTO;
import com.larcangeli.monolith.recommendation.shared.IRecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
class RecommendationController implements IRecommendationController {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationController.class);

    private final IRecommendationService recommendationService;

    @Autowired
    public RecommendationController(IRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    public RecommendationDTO createRecommendation(@PathVariable Long productId, @RequestBody RecommendationDTO recommendation){
        LOG.debug("deleteCompositeProduct: Creates the recommendation with ID: {}", recommendation.recommendationId());
        RecommendationDTO r = recommendationService.save(recommendation);
        LOG.debug("deleteCompositeProduct: recommendations created with ID: {}", recommendation.recommendationId());
        return r;
    }

    @Override
    public void deleteRecommendation(@PathVariable Long productId, @PathVariable Long recommendationId){
        LOG.debug("deleteCompositeProduct: Deletes the recommendation with ID: {}", recommendationId);
        recommendationService.deleteById(recommendationId);
        LOG.debug("deleteCompositeProduct: recommendations deleted for ID: {}", recommendationId);
    }

}
