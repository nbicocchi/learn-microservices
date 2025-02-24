package com.nbicocchi.monolith.recommendation.web.controllers;

import com.nbicocchi.monolith.recommendation.shared.RecommendationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

public interface IRecommendationController {

    @PostMapping(value = "/products/{productId}/recommendations")
    String createRecommendation(Model model, @PathVariable Long productId, @ModelAttribute("recommendation") RecommendationDTO recommendation);

    @DeleteMapping(value = "/products/{productId}/recommendations/{recommendationId}")
    String deleteRecommendation(Model model, @PathVariable Long productId, @PathVariable Long recommendationId);
}
