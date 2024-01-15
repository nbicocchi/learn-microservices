package com.nbicocchi.microservices.composite.product.services;

import java.util.List;
import java.util.stream.Collectors;

import com.nbicocchi.api.core.product.ProductDto;
import com.nbicocchi.api.core.recommendation.RecommendationDto;
import com.nbicocchi.api.core.review.ReviewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.nbicocchi.api.composite.product.*;
import com.nbicocchi.api.exceptions.NotFoundException;
import com.nbicocchi.util.http.ServiceUtil;

@RestController
public class ProductCompositeControllerImpl implements ProductCompositeController {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeControllerImpl.class);

  private final ServiceUtil serviceUtil;
  private ProductCompositeIntegration integration;

  @Autowired
  public ProductCompositeControllerImpl(
    ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
    
    this.serviceUtil = serviceUtil;
    this.integration = integration;
  }

  @Override
  public void createProduct(ProductAggregateDto body) {

    try {

      LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getProductId());

      ProductDto productDto = new ProductDto(body.getProductId(), body.getName(), body.getWeight(), null);
      integration.createProduct(productDto);

      if (body.getRecommendations() != null) {
        body.getRecommendations().forEach(r -> {
          RecommendationDto recommendationDto = new RecommendationDto(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
          integration.createRecommendation(recommendationDto);
        });
      }

      if (body.getReviews() != null) {
        body.getReviews().forEach(r -> {
          ReviewDto reviewDto = new ReviewDto(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
          integration.createReview(reviewDto);
        });
      }

      LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.getProductId());

    } catch (RuntimeException re) {
      LOG.warn("createCompositeProduct failed", re);
      throw re;
    }
  }


  @Override
  public ProductAggregateDto getProduct(int productId) {

    LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);

    ProductDto productDto = integration.getProduct(productId);
    if (productDto == null) {
      throw new NotFoundException("No product found for productId: " + productId);
    }

    List<RecommendationDto> recommendationDtos = integration.getRecommendations(productId);

    List<ReviewDto> reviewDtos = integration.getReviews(productId);

    LOG.debug("getCompositeProduct: aggregate entity found for productId: {}", productId);

    return createProductAggregate(productDto, recommendationDtos, reviewDtos, serviceUtil.getServiceAddress());
  }

  @Override
  public void deleteProduct(int productId) {

    LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

    integration.deleteProduct(productId);

    integration.deleteRecommendations(productId);

    integration.deleteReviews(productId);

    LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
  }

  private ProductAggregateDto createProductAggregate(
    ProductDto productDto,
    List<RecommendationDto> recommendationDtos,
    List<ReviewDto> reviewDtos,
    String serviceAddress) {

    // 1. Setup product info
    int productId = productDto.getProductId();
    String name = productDto.getName();
    int weight = productDto.getWeight();

    // 2. Copy summary recommendation info, if available
    List<RecommendationSummaryDto> recommendationSummaries = (recommendationDtos == null) ? null :
      recommendationDtos.stream()
        .map(r -> new RecommendationSummaryDto(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
        .collect(Collectors.toList());

    // 3. Copy summary review info, if available
    List<ReviewSummaryDto> reviewSummaries = (reviewDtos == null) ? null :
      reviewDtos.stream()
        .map(r -> new ReviewSummaryDto(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
        .collect(Collectors.toList());

    // 4. Create info regarding the involved microservices addresses
    String productAddress = productDto.getServiceAddress();
    String reviewAddress = (reviewDtos != null && reviewDtos.size() > 0) ? reviewDtos.get(0).getServiceAddress() : "";
    String recommendationAddress = (recommendationDtos != null && recommendationDtos.size() > 0) ? recommendationDtos.get(0).getServiceAddress() : "";
    ServiceAddressesDto serviceAddressesDto = new ServiceAddressesDto(serviceAddress, productAddress, reviewAddress, recommendationAddress);

    return new ProductAggregateDto(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddressesDto);
  }
}
