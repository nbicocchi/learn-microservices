package com.nbicocchi.microservices.core.review;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgresTestBase {

  static PostgreSQLContainer<?> database = new PostgreSQLContainer<>(
          "postgres:latest"
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
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("spring.datasource.password", database::getPassword);
  }

}
