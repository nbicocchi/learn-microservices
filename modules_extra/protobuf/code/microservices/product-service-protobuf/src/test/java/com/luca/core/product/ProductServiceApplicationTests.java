package com.luca.core.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROTOBUF;
import static reactor.core.publisher.Mono.just;

import com.google.protobuf.InvalidProtocolBufferException;
import com.luca.product.protobuf.ProductProto.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.luca.error.protobuf.ErrorInfoProto.ErrorInfo;
import com.luca.core.product.persistence.ProductRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductServiceApplicationTests extends MongoDbTestBase {

  @Autowired private WebTestClient client;

  @Autowired private ProductRepository repository;

  @BeforeEach
  void setupDb() {
    repository.deleteAll();
  }

  @Test
  void getProductById() throws InvalidProtocolBufferException {
    //
    int productId = 1;

    postAndVerifyProduct(productId, OK);

    assertTrue(repository.findByProductId(productId).isPresent());

    byte[] a = getAndVerifyProduct(productId, OK).returnResult().getResponseBody();
    Product product = Product.parseFrom(a);

    assertEquals(productId, product.getProductId());
  }
  @Test
  void duplicateError() throws InvalidProtocolBufferException {

    int productId = 1;

    postAndVerifyProduct(productId, OK);

    assertTrue(repository.findByProductId(productId).isPresent());

    ErrorInfo errorInfo = ErrorInfo.newBuilder()
            .setPath("/product")
            .setMessage("Duplicate key, Product Id: " + productId)
            .build();

    byte[] a = postAndVerifyProduct(productId, UNPROCESSABLE_ENTITY).returnResult().getResponseBody();
    ErrorInfo error = ErrorInfo.parseFrom(a);
    System.out.println(error);
    assertEquals(errorInfo.getPath(), error.getPath());
    assertEquals(errorInfo.getMessage(), error.getMessage());
  }

  @Test
  void deleteProduct() {

    int productId = 1;

    postAndVerifyProduct(productId, OK);
    assertTrue(repository.findByProductId(productId).isPresent());

    deleteAndVerifyProduct(productId, OK);
    assertFalse(repository.findByProductId(productId).isPresent());

    deleteAndVerifyProduct(productId, OK);
  }

  @Test
  void getProductInvalidParameterString() throws InvalidProtocolBufferException {

    getAndVerifyProduct("/no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/product/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
  }

  @Test
  void getProductNotFound() throws InvalidProtocolBufferException {

    int productIdNotFound = 13;
    ErrorInfo errorInfo = ErrorInfo.newBuilder()
            .setPath("/product/" + productIdNotFound)
            .setMessage("No product found for productId: " + productIdNotFound)
            .build();

    byte[] a = getAndVerifyProduct(productIdNotFound, NOT_FOUND).returnResult().getResponseBody();
    ErrorInfo error = ErrorInfo.parseFrom(a);

    assertEquals(errorInfo.getPath(), error.getPath());
    assertEquals(errorInfo.getMessage(), error.getMessage());

  }

  @Test
  void getProductInvalidParameterNegativeValue() throws InvalidProtocolBufferException {

    int productIdInvalid = -1;

    ErrorInfo errorInfo = ErrorInfo.newBuilder()
            .setPath("/product/" + productIdInvalid)
            .setMessage("Invalid productId: " + productIdInvalid)
            .build();

    byte[] a = getAndVerifyProduct(productIdInvalid, UNPROCESSABLE_ENTITY).returnResult().getResponseBody();
    ErrorInfo error = ErrorInfo.parseFrom(a);
    assertEquals(errorInfo.getPath(), error.getPath());
    assertEquals(errorInfo.getMessage(), error.getMessage());
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
    return getAndVerifyProduct("/" + productId, expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(String productIdPath, HttpStatus expectedStatus) {
    MediaType contentType = null;
    if (expectedStatus.is2xxSuccessful()){
      contentType = APPLICATION_PROTOBUF;
    } else {
      contentType = APPLICATION_JSON;
    }
    return client.get()
            .uri("/product" + productIdPath)
            .accept(APPLICATION_JSON, APPLICATION_PROTOBUF)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .expectHeader().contentType(contentType)
            .expectBody();
  }

  private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {

    Product product = Product.newBuilder()
            .setProductId(productId)
            .setName("Name " + productId)
            .setWeight(productId)
            .setServiceAddress("SA")
            .build();
    return client.post()
            .uri("/product")
            .body(just(product), Product.class)
            .accept(APPLICATION_PROTOBUF)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .expectHeader().contentType(APPLICATION_PROTOBUF)
            .expectBody();
  }

  private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {

    return client.delete()
            .uri("/product/" + productId)
            .accept(APPLICATION_PROTOBUF)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .expectBody();
  }
}