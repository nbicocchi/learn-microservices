package com.nbicocchi.monolith.adapters.api.controller;

import com.nbicocchi.monolith.adapters.api.dto.ProductAggregateDTO;
import com.nbicocchi.monolith.adapters.api.dto.RecommendationDTO;
import com.nbicocchi.monolith.adapters.api.dto.ReviewDTO;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


public interface IThymeleafController {

    @GetMapping("/")
    String homePage(Model model);

    @GetMapping("/products")
    String getAllProducts(Model model);

    @GetMapping(value = "/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    String getProduct(Model model, @PathVariable Long productId);

    @PostMapping(value = "/products")
    String createProduct(Model model, @ModelAttribute("product") ProductAggregateDTO request);

    @DeleteMapping(value = "/products/{productId}")
    String deleteProduct(Model model, @PathVariable Long productId);

    @PostMapping(value = "/products/{productId}/recommendations")
    String createRecommendation(Model model, @PathVariable Long productId, @ModelAttribute("recommendation") RecommendationDTO recommendation);

    @DeleteMapping(value = "/products/{productId}/recommendations/{recommendationId}")
    String deleteRecommendation(Model model, @PathVariable Long productId, @PathVariable Long recommendationId);

    @PostMapping(value = "/products/{productId}/reviews")
    String createReview(Model model, @PathVariable Long productId, @ModelAttribute("review") ReviewDTO review);

    @DeleteMapping(value = "/products/{productId}/reviews/{reviewId}")
    String deleteReview(Model model, @PathVariable Long productId, @PathVariable Long reviewId);

}