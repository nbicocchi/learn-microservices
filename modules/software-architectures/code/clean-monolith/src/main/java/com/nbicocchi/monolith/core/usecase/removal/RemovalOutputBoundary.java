package com.nbicocchi.monolith.core.usecase.removal;

/**
 * A simple boundary that allows the Controller in the adapter layer to use all the underlying functions
 * without a direct interaction with the Use Case layer
 */
public interface RemovalOutputBoundary {
    void deleteProduct(Long productId);

    void deleteRecommendation(Long productId, Long recommendationId);

    void deleteReview(Long productId, Long reviewId);
}
