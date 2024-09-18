package com.larcangeli.monolith.review.web.controllers;

import com.larcangeli.monolith.review.shared.ReviewDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IReviewController {

    @PostMapping(value = "/product-composite/{productId}/reviews", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ReviewDTO createReview(@RequestBody ReviewDTO review);

    @DeleteMapping(value = "/product-composite/{productId}/reviews/{reviewId}")
    void deleteReview(@PathVariable Long reviewId);

}
