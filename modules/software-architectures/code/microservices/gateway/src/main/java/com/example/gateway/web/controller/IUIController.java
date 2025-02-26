package com.example.gateway.web.controller;

import com.example.gateway.web.dto.ProductDTO;
import com.example.gateway.web.dto.RecommendationDTO;
import com.example.gateway.web.dto.ReviewDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

public interface IUIController {

    @GetMapping("/")
    String homePage(Model model);

    @GetMapping("/products")
    String getAllProducts(Model model);

    @GetMapping(value = "/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    String getProduct(Model model, @PathVariable Long productId);

    @PostMapping(value = "/products")
    String createProduct(Model model, @ModelAttribute("product") ProductDTO request);

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
