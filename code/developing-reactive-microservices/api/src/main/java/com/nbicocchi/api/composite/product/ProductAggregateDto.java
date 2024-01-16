package com.nbicocchi.api.composite.product;

import java.util.List;

public class ProductAggregateDto {
  private final int productId;
  private final String name;
  private final int weight;
  private final List<RecommendationSummaryDto> recommendations;
  private final List<ReviewSummaryDto> reviews;
  private final ServiceAddressesDto serviceAddressesDto;

  public ProductAggregateDto() {
    productId = 0;
    name = null;
    weight = 0;
    recommendations = null;
    reviews = null;
    serviceAddressesDto = null;
  }

  public ProductAggregateDto(
    int productId,
    String name,
    int weight,
    List<RecommendationSummaryDto> recommendations,
    List<ReviewSummaryDto> reviews,
    ServiceAddressesDto serviceAddressesDto) {

    this.productId = productId;
    this.name = name;
    this.weight = weight;
    this.recommendations = recommendations;
    this.reviews = reviews;
    this.serviceAddressesDto = serviceAddressesDto;
  }

  public int getProductId() {
    return productId;
  }

  public String getName() {
    return name;
  }

  public int getWeight() {
    return weight;
  }

  public List<RecommendationSummaryDto> getRecommendations() {
    return recommendations;
  }

  public List<ReviewSummaryDto> getReviews() {
    return reviews;
  }

  public ServiceAddressesDto getServiceAddresses() {
    return serviceAddressesDto;
  }
}
