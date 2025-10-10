package com.example.review.service.web.controller.impl;

import com.example.review.service.service.IReviewService;
import com.example.review.service.web.controller.IReviewController;
import com.example.review.service.web.dto.ReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class ReviewController implements IReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    private final IReviewService reviewService;

    @Autowired
    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public List<ReviewDTO> getReviews(@PathVariable Long productId) {
        return reviewService.findReviewsByProductId(productId);
    }

    @Override
    public ReviewDTO createReview(@RequestBody ReviewDTO review){
        LOG.debug("createReview: Creates the review with ID: {}", review.reviewId());
        ReviewDTO r = reviewService.save(review);
        LOG.debug("createReview: review created with ID: {}", review.reviewId());
        return r;
    }

    @Override
    public void deleteById(@PathVariable Long productId, @PathVariable Long reviewId){
        LOG.debug("deleteById: Deletes the review with ID: {}", reviewId);
        reviewService.deleteById(reviewId);
        LOG.debug("deleteById: review deleted for ID: {}", reviewId);
    }

    @Override
    public void deleteAllReviews(@PathVariable Long productId){
        LOG.debug("deleteAllReviews: Deletes the review with ID: {}", productId);
        reviewService.deleteReviews(productId);
        LOG.debug("deleteAllReviews: review deleted for ID: {}", productId);
    }

}