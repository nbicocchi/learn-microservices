package com.larcangeli.monolith.review.web.controllers;

import com.larcangeli.monolith.review.shared.ReviewDTO;
import com.larcangeli.monolith.review.shared.IReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
class ReviewController implements IReviewController{

    private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    private final IReviewService reviewService;

    @Autowired
    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public ReviewDTO createReview(@RequestBody ReviewDTO review){
        LOG.debug("deleteCompositeProduct: Creates the review with ID: {}", review.reviewId());
        ReviewDTO r = reviewService.save(review);
        LOG.debug("deleteCompositeProduct: review created with ID: {}", review.reviewId());
        return r;
    }

    @Override
    public void deleteReview(@PathVariable Long reviewId){
        LOG.debug("deleteCompositeProduct: Deletes the review with ID: {}", reviewId);
        reviewService.deleteById(reviewId);
        LOG.debug("deleteCompositeProduct: review deleted for ID: {}", reviewId);
    }

}
