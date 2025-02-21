package com.nbicocchi.monolith.core.usecase.removal;

import org.springframework.stereotype.Component;

/**
 * A simple use case that implements the logic of deleting entities
 */
@Component
public class RemovalInteractor implements RemovalInputBoundary {

    RemovalOutputBoundary removalOutputBoundary;

    public RemovalInteractor(RemovalOutputBoundary removalOutputBoundary) {
        this.removalOutputBoundary = removalOutputBoundary;
    }

    @Override
    public void deleteProduct(Long productId) {
        removalOutputBoundary.deleteProduct(productId);
    }

    @Override
    public void deleteRecommendation(Long productId, Long recommendationId) {
        removalOutputBoundary.deleteRecommendation(productId, recommendationId);
    }

    @Override
    public void deleteReview(Long productId, Long reviewId) {
        removalOutputBoundary.deleteReview(productId, reviewId);
    }
}
