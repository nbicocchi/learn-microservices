package com.nbicocchi.microservices.core.review.controller;

import java.util.List;

import com.nbicocchi.api.core.review.ReviewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import com.nbicocchi.api.core.review.ReviewController;
import com.nbicocchi.api.exceptions.InvalidInputException;
import com.nbicocchi.microservices.core.review.persistence.ReviewEntity;
import com.nbicocchi.microservices.core.review.persistence.ReviewRepository;
import com.nbicocchi.util.http.ServiceUtil;

@RestController
public class ReviewControllerImpl implements ReviewController {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewControllerImpl.class);

  private final ReviewRepository repository;

  private final ReviewMapper mapper;

  private final ServiceUtil serviceUtil;

  @Autowired
  public ReviewControllerImpl(ReviewRepository repository, ReviewMapper mapper, ServiceUtil serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public ReviewDto createReview(ReviewDto body) {
    try {
      ReviewEntity entity = mapper.apiToEntity(body);
      ReviewEntity newEntity = repository.save(entity);

      LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return mapper.entityToApi(newEntity);

    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
    }
  }

  @Override
  public List<ReviewDto> getReviews(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    
    List<ReviewEntity> entityList = repository.findByProductId(productId);
    List<ReviewDto> list = mapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("getReviews: response size: {}", list.size());

    return list;
  }

  @Override
  public void deleteReviews(int productId) {
    LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}