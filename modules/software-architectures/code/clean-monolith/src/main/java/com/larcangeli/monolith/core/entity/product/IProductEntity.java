package com.larcangeli.monolith.core.entity.product;

import com.larcangeli.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.larcangeli.monolith.core.entity.review.impl.ReviewEntity;

import java.util.Set;

public interface IProductEntity {
    Long getProductId();
    Integer getVersion();
    String getName();
    int getWeight();
    Set<ReviewEntity> getReviews();
    Set<RecommendationEntity> getRecommendations();
}
