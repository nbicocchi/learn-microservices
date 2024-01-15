package com.nbicocchi.api.composite.product;

public class RecommendationSummaryDto {

  private final int recommendationId;
  private final String author;
  private final int rate;
  private final String content;

  public RecommendationSummaryDto() {
    this.recommendationId = 0;
    this.author = null;
    this.rate = 0;
    this.content = null;
  }

  public RecommendationSummaryDto(int recommendationId, String author, int rate, String content) {
    this.recommendationId = recommendationId;
    this.author = author;
    this.rate = rate;
    this.content = content;
  }

  public int getRecommendationId() {
    return recommendationId;
  }

  public String getAuthor() {
    return author;
  }

  public int getRate() {
    return rate;
  }

  public String getContent() {
    return content;
  }
}
