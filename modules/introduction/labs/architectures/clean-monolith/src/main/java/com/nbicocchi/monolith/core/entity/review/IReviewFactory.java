package com.nbicocchi.monolith.core.entity.review;

public interface IReviewFactory {
    IReviewEntity createReview(String author, String subject, String content);
    IReviewEntity createReview(Long productId, String author, String subject, String content);
    IReviewEntity createReview(Long reviewId, Long productId, String author, String subject, String content);
}
