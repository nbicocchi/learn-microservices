package com.example.review.service.web.controller;

import com.example.review.service.web.dto.ReviewDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products/{productId}")
public interface IReviewController {

    @GetMapping(value = "/reviews")
    List<ReviewDTO> getReviews(@PathVariable Long productId);

    @PostMapping(value = "/reviews", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ReviewDTO createReview(@RequestBody ReviewDTO review);

    @DeleteMapping(value = "/reviews")
    void deleteAllReviews(@PathVariable Long productId);

    @DeleteMapping(value = "/reviews/{reviewId}")
    void deleteById(@PathVariable Long productId, @PathVariable Long reviewId);

}
