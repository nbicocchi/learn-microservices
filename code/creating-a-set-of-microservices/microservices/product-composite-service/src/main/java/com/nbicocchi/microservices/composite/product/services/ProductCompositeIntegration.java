package com.nbicocchi.microservices.composite.product.services;

import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nbicocchi.api.core.product.ProductDto;
import com.nbicocchi.api.core.recommendation.RecommendationDto;
import com.nbicocchi.api.core.review.ReviewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.nbicocchi.api.core.product.ProductController;
import com.nbicocchi.api.core.recommendation.RecommendationController;
import com.nbicocchi.api.core.review.ReviewController;
import com.nbicocchi.api.exceptions.InvalidInputException;
import com.nbicocchi.api.exceptions.NotFoundException;
import com.nbicocchi.util.http.HttpErrorInfo;

@Component
public class ProductCompositeIntegration implements ProductController, RecommendationController, ReviewController {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;

  private final String productServiceUrl;
  private final String recommendationServiceUrl;
  private final String reviewServiceUrl;

  @Autowired
  public ProductCompositeIntegration(
    RestTemplate restTemplate,
    ObjectMapper mapper,
    @Value("${app.product-service.host}") String productServiceHost,
    @Value("${app.product-service.port}") int productServicePort,
    @Value("${app.recommendation-service.host}") String recommendationServiceHost,
    @Value("${app.recommendation-service.port}") int recommendationServicePort,
    @Value("${app.review-service.host}") String reviewServiceHost,
    @Value("${app.review-service.port}") int reviewServicePort) {

    this.restTemplate = restTemplate;
    this.mapper = mapper;

    productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product";
    recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
    reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
  }

  @Override
  public ProductDto createProduct(ProductDto body) {

    try {
      String url = productServiceUrl;
      LOG.debug("Will post a new product to URL: {}", url);

      ProductDto productDto = restTemplate.postForObject(url, body, ProductDto.class);
      LOG.debug("Created a product with id: {}", productDto.getProductId());

      return productDto;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public ProductDto getProduct(int productId) {

    try {
      String url = productServiceUrl + "/" + productId;
      LOG.debug("Will call the getProduct API on URL: {}", url);

      ProductDto productDto = restTemplate.getForObject(url, ProductDto.class);
      LOG.debug("Found a product with id: {}", productDto.getProductId());

      return productDto;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public void deleteProduct(int productId) {
    try {
      String url = productServiceUrl + "/" + productId;
      LOG.debug("Will call the deleteProduct API on URL: {}", url);

      restTemplate.delete(url);

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public RecommendationDto createRecommendation(RecommendationDto body) {

    try {
      String url = recommendationServiceUrl;
      LOG.debug("Will post a new recommendation to URL: {}", url);

      RecommendationDto recommendationDto = restTemplate.postForObject(url, body, RecommendationDto.class);
      LOG.debug("Created a recommendation with id: {}", recommendationDto.getProductId());

      return recommendationDto;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public List<RecommendationDto> getRecommendations(int productId) {

    try {
      String url = recommendationServiceUrl + "?productId=" + productId;

      LOG.debug("Will call the getRecommendations API on URL: {}", url);
      List<RecommendationDto> recommendationDtos = restTemplate
        .exchange(url, GET, null, new ParameterizedTypeReference<List<RecommendationDto>>() {})
        .getBody();

      LOG.debug("Found {} recommendations for a product with id: {}", recommendationDtos.size(), productId);
      return recommendationDtos;

    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void deleteRecommendations(int productId) {
    try {
      String url = recommendationServiceUrl + "?productId=" + productId;
      LOG.debug("Will call the deleteRecommendations API on URL: {}", url);

      restTemplate.delete(url);

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public ReviewDto createReview(ReviewDto body) {

    try {
      String url = reviewServiceUrl;
      LOG.debug("Will post a new review to URL: {}", url);

      ReviewDto reviewDto = restTemplate.postForObject(url, body, ReviewDto.class);
      LOG.debug("Created a review with id: {}", reviewDto.getProductId());

      return reviewDto;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public List<ReviewDto> getReviews(int productId) {

    try {
      String url = reviewServiceUrl + "?productId=" + productId;

      LOG.debug("Will call the getReviews API on URL: {}", url);
      List<ReviewDto> reviewDtos = restTemplate
        .exchange(url, GET, null, new ParameterizedTypeReference<List<ReviewDto>>() {})
        .getBody();

      LOG.debug("Found {} reviews for a product with id: {}", reviewDtos.size(), productId);
      return reviewDtos;

    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void deleteReviews(int productId) {
    try {
      String url = reviewServiceUrl + "?productId=" + productId;
      LOG.debug("Will call the deleteReviews API on URL: {}", url);

      restTemplate.delete(url);

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
    switch (HttpStatus.resolve(ex.getStatusCode().value())) {

      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(ex));

      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(ex));

      default:
        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        LOG.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
  }

  private String getErrorMessage(HttpClientErrorException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
      return ex.getMessage();
    }
  }
}
