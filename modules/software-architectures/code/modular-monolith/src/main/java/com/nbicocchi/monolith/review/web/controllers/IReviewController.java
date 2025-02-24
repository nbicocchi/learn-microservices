package com.nbicocchi.monolith.review.web.controllers;

import com.nbicocchi.monolith.review.shared.ReviewDTO;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IReviewController {

    @PostMapping(value = "/products/{productId}/reviews")
    String createReview(Model model, @PathVariable Long productId, @ModelAttribute("review") ReviewDTO review);

    @DeleteMapping(value = "/products/{productId}/reviews/{reviewId}")
    String deleteReview(Model model, @PathVariable Long productId, @PathVariable Long reviewId);

}
