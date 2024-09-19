package com.larcangeli.monolith.recommendation.web.controllers;

import com.larcangeli.monolith.recommendation.shared.RecommendationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface IRecommendationController {

    @PostMapping(value = "/product-composite/{productId}/recommendations", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    RecommendationDTO createRecommendation(@PathVariable Long productId, @RequestBody RecommendationDTO recommendation);

    @DeleteMapping(value = "/product-composite/{productId}/recommendations/{recommendationId}")
    void deleteRecommendation(@PathVariable Long productId, @PathVariable Long recommendationId);


}
