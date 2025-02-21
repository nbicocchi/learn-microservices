package com.nbicocchi.monolith.core.usecase.retrieval;

import com.nbicocchi.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.nbicocchi.monolith.core.entity.review.impl.ReviewEntity;
import com.nbicocchi.monolith.core.entity.product.IProductEntity;

import java.util.List;
import java.util.Set;

public interface RetrievalOutputBoundary {
    IProductEntity getProduct(Long productId);

    List<IProductEntity> getAllProducts();

    Set<RecommendationEntity> findRecommendationsByProductId(Long productId);

    Set<ReviewEntity> findReviewsByProductId(Long productId);
}
