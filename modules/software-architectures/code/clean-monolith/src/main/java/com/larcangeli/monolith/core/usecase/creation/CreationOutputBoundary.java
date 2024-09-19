package com.larcangeli.monolith.core.usecase.creation;

import com.larcangeli.monolith.core.entity.product.IProductEntity;
import com.larcangeli.monolith.core.entity.recommendation.IRecommendationEntity;
import com.larcangeli.monolith.core.entity.review.IReviewEntity;

public interface CreationOutputBoundary {

    IProductEntity saveProduct(IProductEntity product);

    IRecommendationEntity saveRecommendation(IRecommendationEntity recommendation);

    IReviewEntity saveReview(IReviewEntity review);

}
