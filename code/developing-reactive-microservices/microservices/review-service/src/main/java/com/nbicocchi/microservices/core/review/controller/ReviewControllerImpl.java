package com.nbicocchi.microservices.core.review.controller;

import com.nbicocchi.api.core.review.ReviewController;
import com.nbicocchi.api.core.review.ReviewDto;
import com.nbicocchi.api.exceptions.InvalidInputException;
import com.nbicocchi.microservices.core.review.persistence.ReviewEntity;
import com.nbicocchi.microservices.core.review.persistence.ReviewRepository;
import com.nbicocchi.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.logging.Level;

@RestController
public class ReviewControllerImpl implements ReviewController {
  private static final Logger LOG = LoggerFactory.getLogger(ReviewControllerImpl.class);
  private final ReviewRepository repository;
  private final ReviewMapper mapper;
  private final ServiceUtil serviceUtil;
  private final Scheduler jdbcScheduler;

  public ReviewControllerImpl(ReviewRepository repository, ReviewMapper mapper, ServiceUtil serviceUtil, Scheduler jdbcScheduler) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
    this.jdbcScheduler = jdbcScheduler;
  }

  @Override
  public Mono<ReviewDto> createReview(ReviewDto body) {
    if (body.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + body.getProductId());
    }
    return Mono.fromCallable(() -> internalCreateReview(body))
            .subscribeOn(jdbcScheduler);
  }

  @Override
  public Flux<ReviewDto> getReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.info("Will get reviews for product with id={}", productId);
    return Mono.fromCallable(() -> internalGetReviews(productId))
            .flatMapMany(Flux::fromIterable)
            .log(LOG.getName(), Level.FINE)
            .subscribeOn(jdbcScheduler);
  }

  @Override
  public Mono<Void> deleteReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    return Mono.fromRunnable(() -> internalDeleteReviews(productId))
            .subscribeOn(jdbcScheduler)
            .then();
  }

  private ReviewDto internalCreateReview(ReviewDto body) {
    try {
      ReviewEntity entity = mapper.apiToEntity(body);
      ReviewEntity newEntity = repository.save(entity);

      LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return mapper.entityToApi(newEntity);

    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
    }
  }

  private List<ReviewDto> internalGetReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    
    List<ReviewEntity> entityList = repository.findByProductId(productId);
    List<ReviewDto> list = mapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("getReviews: response size: {}", list.size());

    return list;
  }

  private void internalDeleteReviews(int productId) {
    LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}