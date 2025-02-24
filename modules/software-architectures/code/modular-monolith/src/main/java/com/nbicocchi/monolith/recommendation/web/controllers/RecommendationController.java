package com.nbicocchi.monolith.recommendation.web.controllers;

import com.nbicocchi.monolith.recommendation.shared.RecommendationDTO;
import com.nbicocchi.monolith.recommendation.shared.IRecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
class RecommendationController implements IRecommendationController {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationController.class);

    private final IRecommendationService recommendationService;

    @Autowired
    public RecommendationController(IRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    public String createRecommendation(Model model, @PathVariable Long productId, @ModelAttribute("recommendation") RecommendationDTO recommendation){
        try{
            LOG.debug("createRecommendation: creates a new recommendation entity for productId: {}", productId);
            RecommendationDTO r = recommendationService.save(recommendation);
            LOG.debug("createRecommendation: recommendation created");
        }catch (RuntimeException re) {
            LOG.warn("createRecommendation failed", re);
            throw re;
        }
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

    @Override
    public String deleteRecommendation(Model model, @PathVariable Long productId, @PathVariable Long recommendationId){
        LOG.debug("deleteRecommendation: Deletes the recommendation with ID: {}", recommendationId);
        try{
            recommendationService.deleteById(recommendationId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Recommendation with ID: " + recommendationId + " not found");
        }
        LOG.debug("deleteRecommendation: recommendation deleted for ID: {}", recommendationId);
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

}
