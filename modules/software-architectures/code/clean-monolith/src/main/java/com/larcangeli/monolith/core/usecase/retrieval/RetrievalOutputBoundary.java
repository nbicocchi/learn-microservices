package com.larcangeli.monolith.core.usecase.retrieval;

import com.larcangeli.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.larcangeli.monolith.core.entity.review.impl.ReviewEntity;
import com.larcangeli.monolith.core.entity.product.IProductEntity;

import java.util.List;
import java.util.Set;

public interface RetrievalOutputBoundary {
    IProductEntity getProduct(Long productId);

    List<IProductEntity> getAllProducts();

    Set<RecommendationEntity> findRecommendationsByProductId(Long productId);

    Set<ReviewEntity> findReviewsByProductId(Long productId);
}
