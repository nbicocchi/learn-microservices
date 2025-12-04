package com.nbicocchi.monolith.core.entity.recommendation;

public interface IRecommendationEntity {
    Long getProductId();
    Long getRecommendationId();
    Integer getVersion();
    String getAuthor();
    int getRating();
    String getContent();

}
