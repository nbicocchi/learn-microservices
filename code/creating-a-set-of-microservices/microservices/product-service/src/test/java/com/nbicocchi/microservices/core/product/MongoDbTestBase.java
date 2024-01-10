package com.nbicocchi.microservices.core.product;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public abstract class MongoDbTestBase {

  static MongoDBContainer database = new MongoDBContainer (
          "mongo:latest"
  );

  @BeforeAll
  static void beforeAll() {
    database.start();
  }

  @AfterAll
  static void afterAll() {
    database.stop();
  }
  
  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.host", database::getContainerIpAddress);
    registry.add("spring.data.mongodb.port", () -> database.getMappedPort(27017));
    registry.add("spring.data.mongodb.database", () -> "test");
  }
}
