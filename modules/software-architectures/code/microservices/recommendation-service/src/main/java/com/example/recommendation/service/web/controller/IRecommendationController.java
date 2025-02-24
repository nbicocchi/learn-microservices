package com.example.recommendation.service.web.controller;

import com.example.recommendation.service.web.dto.RecommendationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products/{productId}")
public interface IRecommendationController {

    @PostMapping(value = "/recommendations", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    RecommendationDTO createRecommendation( @RequestBody RecommendationDTO recommendation);

    @GetMapping(value = "/recommendations")
    List<RecommendationDTO> getRecommendations(@PathVariable Long productId);

    @DeleteMapping(value = "/recommendations/{recommendationId}")
    void deleteRecommendation( @PathVariable Long productId, @PathVariable Long recommendationId);

    @DeleteMapping(value = "/recommendations")
    void deleteAllRecommendations(@PathVariable Long productId);



}