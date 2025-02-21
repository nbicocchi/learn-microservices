package com.nbicocchi.monolith.core.usecase.creation;

import com.nbicocchi.monolith.core.entity.product.IProductEntity;
import com.nbicocchi.monolith.core.entity.recommendation.IRecommendationEntity;
import com.nbicocchi.monolith.core.entity.review.IReviewEntity;

/**
 * A simple boundary that allows the Controller in the adapter layer to use all the underlying functions
 * without a direct interaction with the Use Case layer
 */
public interface CreationInputBoundary {
    IProductEntity createProduct(IProductEntity product);

    IRecommendationEntity createRecommendation(IRecommendationEntity recommendation);

    IReviewEntity createReview(IReviewEntity review);
}
