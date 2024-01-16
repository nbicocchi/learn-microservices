package com.nbicocchi.microservices.core.product.controller;

import com.nbicocchi.api.core.product.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.nbicocchi.api.core.product.ProductController;
import com.nbicocchi.api.exceptions.InvalidInputException;
import com.nbicocchi.api.exceptions.NotFoundException;
import com.nbicocchi.microservices.core.product.persistence.ProductEntity;
import com.nbicocchi.microservices.core.product.persistence.ProductRepository;
import com.nbicocchi.util.http.ServiceUtil;

@RestController
public class ProductControllerImpl implements ProductController {

  private static final Logger LOG = LoggerFactory.getLogger(ProductControllerImpl.class);

  private final ServiceUtil serviceUtil;

  private final ProductRepository repository;

  private final ProductMapper mapper;

  @Autowired
  public ProductControllerImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public ProductDto createProduct(ProductDto body) {
    try {
      ProductEntity entity = mapper.apiToEntity(body);
      ProductEntity newEntity = repository.save(entity);

      LOG.debug("createProduct: entity created for productId: {}", body.getProductId());
      return mapper.entityToApi(newEntity);

    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
    }
  }

  @Override
  public ProductDto getProduct(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    ProductEntity entity = repository.findByProductId(productId)
      .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));

    ProductDto response = mapper.entityToApi(entity);
    response.setServiceAddress(serviceUtil.getServiceAddress());

    LOG.debug("getProduct: found productId: {}", response.getProductId());

    return response;
  }

  @Override
  public void deleteProduct(int productId) {
    LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
    repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
  }
}
