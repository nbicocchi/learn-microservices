package com.nbicocchi.monolith.core.entity.product.impl;

import com.nbicocchi.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.nbicocchi.monolith.core.entity.review.impl.ReviewEntity;
import com.nbicocchi.monolith.core.entity.product.IProductEntity;

import java.util.Set;


public class ProductEntity implements IProductEntity {

    Long productId;
    Integer version;
    String name;
    int weight;
    Set<RecommendationEntity> recommendations;
    Set<ReviewEntity> reviews;

    public ProductEntity(){

    }

    public ProductEntity(Long productId, Integer version, String name, int weight, Set<RecommendationEntity> recommendations, Set<ReviewEntity> reviews) {
        this.productId = productId;
        this.version = version;
        this.name = name;
        this.weight = weight;
        this.recommendations = recommendations;
        this.reviews = reviews;
    }

    @Override
    public Long getProductId() {
        return this.productId;
    }

    @Override
    public Integer getVersion() {
        return this.version;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public Set<ReviewEntity> getReviews() {
        return reviews;
    }

    @Override
    public Set<RecommendationEntity> getRecommendations() {
        return recommendations;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setRecommendations(Set<RecommendationEntity> recommendations) {
        this.recommendations = recommendations;
    }

    public void setReviews(Set<ReviewEntity> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + productId +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", recommendations=" + recommendations +
                ", reviews=" + reviews +
                '}';
    }
}
