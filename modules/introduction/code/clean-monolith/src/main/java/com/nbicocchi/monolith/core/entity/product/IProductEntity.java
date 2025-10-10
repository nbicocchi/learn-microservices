package com.nbicocchi.monolith.core.entity.product;

import com.nbicocchi.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.nbicocchi.monolith.core.entity.review.impl.ReviewEntity;

import java.util.Set;

public interface IProductEntity {
    Long getProductId();
    Integer getVersion();
    String getName();
    int getWeight();
    Set<ReviewEntity> getReviews();
    Set<RecommendationEntity> getRecommendations();
}
