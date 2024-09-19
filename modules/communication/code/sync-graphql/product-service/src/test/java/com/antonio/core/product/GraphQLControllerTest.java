package com.antonio.core.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.antonio.core.product.web.dto.Product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GraphQLControllerTest extends MongoDbTestBase{

    @Autowired private WebTestClient client;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private Product compositeIntegration;

    void setUp() {
        // Mock response
        when(compositeIntegration.getProduct(1))
                .thenReturn(new Product(1, "Sample Product", 1));
    }

    @Test
    void createCompositeProduct1() {

        Product compositeProduct = new Product(1, "Sample Product", 1);

        postAndVerifyProduct(compositeProduct, OK);
    }

    @Test
    public void testGetProductById() throws JsonProcessingException {
        String query = "{ \"query\": \"query GetProduct { getProduct(productId: 1) { name weight productId } }\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(query, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/graphql",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertEquals(200, response.getStatusCodeValue());
    }

    private void postAndVerifyProduct(Product compositeProduct, HttpStatus expectedStatus) {
        String mutation = "mutation { createProduct(body: { productId: " + compositeProduct.getProductId() +
                ", name: \\\"" + compositeProduct.getName() + "\\\", weight: " + compositeProduct.getWeight() + " }) " +
                "{ productId name weight } }";
        String requestBody = "{\"query\":\"" + mutation + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/graphql",
                HttpMethod.POST,
                entity,
                String.class
        );
        response.getStatusCode().is2xxSuccessful();
    }
}
