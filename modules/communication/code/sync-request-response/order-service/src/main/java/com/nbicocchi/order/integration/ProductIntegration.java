package com.nbicocchi.order.integration;

import com.nbicocchi.order.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductIntegration {
    String productServiceHost;
    int productServicePort;

    public ProductIntegration(
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort) {
        this.productServiceHost = productServiceHost;
        this.productServicePort = productServicePort;
    }

    public List<ProductDto> findAll() {
        String url = "http://" + productServiceHost + ":" + productServicePort + "/products";
        RestClient restClient = RestClient.builder().build();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public ProductDto findbyUuid(String uuid) {
        String url = "http://" + productServiceHost + ":" + productServicePort + "/products" + "/" + uuid;
        RestClient restClient = RestClient.builder().build();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
