package com.larcangeli.monolith.core.entity.review;

import com.larcangeli.monolith.core.entity.review.IReviewEntity;

public interface IReviewFactory {
    IReviewEntity createReview(String author, String subject, String content);
    IReviewEntity createReview(Long productId, String author, String subject, String content);
    IReviewEntity createReview(Long reviewId, Long productId, String author, String subject, String content);
}
