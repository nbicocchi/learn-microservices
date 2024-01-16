package com.nbicocchi.microservices.core.recommendation.controller;

import java.util.List;

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
  public RecommendationDto createRecommendation(RecommendationDto body) {
    try {
      RecommendationEntity entity = mapper.apiToEntity(body);
      RecommendationEntity newEntity = repository.save(entity);

      LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
      return mapper.entityToApi(newEntity);

    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
    }
  }

  @Override
  public List<RecommendationDto> getRecommendations(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    
    List<RecommendationEntity> entityList = repository.findByProductId(productId);
    if (entityList.isEmpty()) {
      throw new NotFoundException("No recommendations found for productId: " + productId);
    }

    List<RecommendationDto> recommendationDtoList = mapper.entityListToApiList(entityList);
    recommendationDtoList.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("getRecommendations: response size: {}", recommendationDtoList.size());

    return recommendationDtoList;

  }

  @Override
  public void deleteRecommendations(int productId) {
    LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}
