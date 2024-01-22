package com.nbicocchi.microservices.core.recommendation.controller;

import java.util.List;
import java.util.logging.Level;

import com.nbicocchi.api.core.recommendation.RecommendationDto;
import com.nbicocchi.api.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.nbicocchi.api.core.recommendation.RecommendationController;
import com.nbicocchi.api.exceptions.InvalidInputException;
import com.nbicocchi.microservices.core.recommendation.persistence.RecommendationEntity;
import com.nbicocchi.microservices.core.recommendation.persistence.RecommendationRepository;
import com.nbicocchi.util.http.ServiceUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RecommendationControllerImpl implements RecommendationController {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationControllerImpl.class);

  private final RecommendationRepository repository;

  private final RecommendationMapper mapper;

  private final ServiceUtil serviceUtil;

  @Autowired
  public RecommendationControllerImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Mono<RecommendationDto> createRecommendation(RecommendationDto body) {
    RecommendationEntity entity = mapper.apiToEntity(body);
    return repository.save(entity)
            .log(LOG.getName(), Level.FINE)
            .onErrorMap(
                    DuplicateKeyException.class,
                    ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId()))
            .map(e -> mapper.entityToApi(e));
  }

  @Override
  public Flux<RecommendationDto> getRecommendations(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.info("Will get recommendations for product with id={}", productId);
    return repository.findByProductId(productId)
            .log(LOG.getName(), Level.FINE)
            .map(e -> mapper.entityToApi(e))
            .map(e -> {
              e.setServiceAddress(serviceUtil.getServiceAddress());
              return e;
            });
  }

  @Override
  public Mono<Void> deleteRecommendations(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
    return repository.deleteAll(repository.findByProductId(productId));
  }
}
