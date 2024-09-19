package com.larcangeli.monolith.core.entity.product;


import com.larcangeli.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.larcangeli.monolith.core.entity.review.impl.ReviewEntity;

import java.util.Set;

public interface IProductFactory {
    //IProductEntity createProduct(Long id, Integer version, String name, int weight);
    IProductEntity createProduct(Long id, Integer version, String name, int weight, Set<RecommendationEntity> recommendations, Set<ReviewEntity> reviews);
}
