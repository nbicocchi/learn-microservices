package com.nbicocchi.monolith.core.entity.recommendation.impl;

import com.nbicocchi.monolith.core.entity.recommendation.IRecommendationEntity;

public class RecommendationEntity implements IRecommendationEntity {
    Long recommendationId;
    Long productId;
    Integer version;
    String author;
    private int rating;
    String content;

    public RecommendationEntity(){

    }

    public RecommendationEntity(Integer version, String author, int rating, String content) {
        this.version = version;
        this.author = author;
        this.rating = rating;
        this.content = content;
    }

    public RecommendationEntity(Long productId, Integer version, String author, int rating, String content) {
        this.productId = productId;
        this.version = version;
        this.author = author;
        this.rating = rating;
        this.content = content;
    }

    public RecommendationEntity(Long recommendationId, Long productId, Integer version, String author, int rating, String content) {
        this.recommendationId = recommendationId;
        this.productId = productId;
        this.version = version;
        this.author = author;
        this.rating = rating;
        this.content = content;
    }

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public Long getRecommendationId() {
        return this.recommendationId;
    }

    @Override
    public Integer getVersion() {
        return this.version;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public int getRating() {
        return this.rating;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    public void setRecommendationId(Long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RecommendationEntity{" +
                "recommendationId=" + recommendationId +
                ", productId=" + productId +
                ", version=" + version +
                ", author='" + author + '\'' +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                '}';
    }
}
