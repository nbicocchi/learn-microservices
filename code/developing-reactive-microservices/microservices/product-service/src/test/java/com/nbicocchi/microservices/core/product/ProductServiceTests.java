package com.nbicocchi.microservices.core.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.nbicocchi.microservices.core.product.persistence.ProductRepository;
import com.nbicocchi.microservices.core.product.persistence.ProductEntity;
import org.testcontainers.shaded.org.yaml.snakeyaml.constructor.DuplicateKeyException;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductServiceTests extends MongoDbTestBase {

  @Autowired
  private ProductRepository repository;

  private ProductEntity savedEntity;

  @BeforeEach
  void setupDb() {
    StepVerifier.create(repository.deleteAll()).verifyComplete();

    ProductEntity entity = new ProductEntity(1, "n", 1);
    StepVerifier.create(repository.save(entity))
            .expectNextMatches(createdEntity -> {
              savedEntity = createdEntity;
              return areProductEqual(entity, savedEntity);
            })
            .verifyComplete();
  }


  @Test
  void create() {
    ProductEntity newEntity = new ProductEntity(2, "n", 2);

    StepVerifier.create(repository.save(newEntity))
            .expectNextMatches(createdEntity -> newEntity.getProductId() == createdEntity.getProductId())
            .verifyComplete();

    StepVerifier.create(repository.findById(newEntity.getId()))
            .expectNextMatches(foundEntity -> areProductEqual(newEntity, foundEntity))
            .verifyComplete();

    StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
  }

  @Test
  void update() {
    savedEntity.setName("n2");
    StepVerifier.create(repository.save(savedEntity))
            .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2"))
            .verifyComplete();

    StepVerifier.create(repository.findById(savedEntity.getId()))
            .expectNextMatches(foundEntity ->
                    foundEntity.getVersion() == 1
                            && foundEntity.getName().equals("n2"))
            .verifyComplete();
  }

  @Test
  void delete() {
    StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
    StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
  }

  @Test
  void getByProductId() {

    StepVerifier.create(repository.findByProductId(savedEntity.getProductId()))
            .expectNextMatches(foundEntity -> areProductEqual(savedEntity, foundEntity))
            .verifyComplete();
  }

  @Test
  void duplicateError() {
    ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
    StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
  }

  private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
    return
            (expectedEntity.getId().equals(actualEntity.getId()))
                    && (expectedEntity.getVersion() == actualEntity.getVersion())
                    && (expectedEntity.getProductId() == actualEntity.getProductId())
                    && (expectedEntity.getName().equals(actualEntity.getName()))
                    && (expectedEntity.getWeight() == actualEntity.getWeight());
  }
}
