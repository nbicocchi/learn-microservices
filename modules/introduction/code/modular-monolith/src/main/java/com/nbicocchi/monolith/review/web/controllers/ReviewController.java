package com.nbicocchi.monolith.review.web.controllers;

import com.nbicocchi.monolith.review.shared.ReviewDTO;
import com.nbicocchi.monolith.review.shared.IReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
class ReviewController implements IReviewController{

    private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    private final IReviewService reviewService;

    @Autowired
    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public String createReview(Model model, @PathVariable Long productId, @ModelAttribute("review") ReviewDTO review){
        try{
            LOG.debug("createReview: creates a new review entity for productId: {}", productId);
            ReviewDTO r = reviewService.save(review);
            LOG.debug("createReview: review created");
        }catch (RuntimeException re) {
            LOG.warn("createReview failed", re);
            throw re;
        }
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

    @Override
    public String deleteReview(Model model, @PathVariable Long productId, @PathVariable Long reviewId){
        LOG.debug("deleteReview: Deletes the review with ID: {}", reviewId);
        try{
            reviewService.deleteById(reviewId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Review with ID: " + reviewId + " not found");
        }
        LOG.debug("deleteReview: review deleted for ID: {}", reviewId);
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

}
