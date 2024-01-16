package com.nbicocchi.api.composite.product;

public class ServiceAddressesDto {
  private final String compositeService;
  private final String productService;
  private final String reviewService;
  private final String recommendationService;

  public ServiceAddressesDto() {
    this.compositeService = null;
    this.productService = null;
    this.reviewService = null;
    this.recommendationService = null;
  }

  public ServiceAddressesDto(String compositeService, String productService, String reviewService, String recommendationService) {
    this.compositeService = compositeService;
    this.productService = productService;
    this.reviewService = reviewService;
    this.recommendationService = recommendationService;
  }

  public String getCompositeService() {
    return compositeService;
  }

  public String getProductService() {
    return productService;
  }

  public String getReviewService() {
    return reviewService;
  }

  public String getRecommendationService() {
    return recommendationService;
  }
}
