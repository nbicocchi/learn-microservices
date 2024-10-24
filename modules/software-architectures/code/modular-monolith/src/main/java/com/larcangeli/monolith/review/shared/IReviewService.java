package com.nbicocchi.monolith.review.shared;

import org.springframework.context.event.EventListener;

import java.util.List;

public interface IReviewService {

    ReviewDTO save(ReviewDTO review);

    @EventListener
    void saveReviewOnProductCreation(ReviewDTO review);

    void deleteById(Long reviewId);

    @EventListener
    void deleteReviews(Long productId);

    List<ReviewDTO> findReviewsByProductId(Long productId);
}
