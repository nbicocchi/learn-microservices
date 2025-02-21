package com.nbicocchi.monolith.core.usecase.creation;

import com.nbicocchi.monolith.core.entity.product.IProductEntity;
import com.nbicocchi.monolith.core.entity.recommendation.IRecommendationEntity;
import com.nbicocchi.monolith.core.entity.review.IReviewEntity;

public interface CreationOutputBoundary {

    IProductEntity saveProduct(IProductEntity product);

    IRecommendationEntity saveRecommendation(IRecommendationEntity recommendation);

    IReviewEntity saveReview(IReviewEntity review);

}
