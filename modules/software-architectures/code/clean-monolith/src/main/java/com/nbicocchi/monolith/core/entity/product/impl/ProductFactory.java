package com.nbicocchi.monolith.core.entity.product.impl;

import com.nbicocchi.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.nbicocchi.monolith.core.entity.review.impl.ReviewEntity;
import com.nbicocchi.monolith.core.entity.product.IProductFactory;
import com.nbicocchi.monolith.core.entity.product.IProductEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProductFactory implements IProductFactory {
    /*@Override
    public IProductEntity createProduct(Long productId, Integer version, String name, int weight) {
        return new ProductEntity(productId,version,name,weight);
    }*/

    @Override
    public IProductEntity createProduct(Long productId, Integer version, String name, int weight, Set<RecommendationEntity> recommendations, Set<ReviewEntity> reviews) {
        return new ProductEntity(productId,version,name,weight,recommendations,reviews);
    }
}
