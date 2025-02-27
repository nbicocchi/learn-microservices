package com.example.gateway.web.controller.impl;

import com.example.gateway.web.controller.IUIController;
import com.example.gateway.web.dto.ProductDTO;
import com.example.gateway.web.dto.RecommendationDTO;
import com.example.gateway.web.dto.ReviewDTO;
import com.example.gateway.web.exceptions.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
public class UIController implements IUIController {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String homePage(Model model) {
        model.addAttribute("message", "Hello from Thymeleaf!");
        return "home";
    }

    @Override
    public String getAllProducts(Model model) {
        String url = "http://product-service:8081/products";
        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        model.addAttribute("allProducts", response.getBody());
        return "products"; // Thymeleaf template (products.html)
    }

    @Override
    public String getProduct(Model model, @PathVariable Long productId) {
        if (productId < 0) {
            throw new UnprocessableEntityException("Invalid productId: " + productId);
        }
        String url = UriComponentsBuilder.fromHttpUrl("http://product-service:8081/products/{productId}")
                .buildAndExpand(productId)
                .toUriString();
        ResponseEntity<ProductDTO> response = restTemplate.getForEntity(url, ProductDTO.class);
        model.addAttribute("product", response.getBody());
        return "product_item"; // Thymeleaf template (product_item.html)
    }

    @Override
    public String createProduct(Model model, @ModelAttribute("product") ProductDTO request) {
        String url = "http://product-service:8081/products";
        restTemplate.postForEntity(url, request, ProductDTO.class); //create new product
        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange(
                "http://product-service:8081/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        model.addAttribute("allProducts", response.getBody());
        return "products"; // Thymeleaf template (products.html)
    }

    @Override
    public String deleteProduct(Model model, @PathVariable Long productId) {
        String url = "http://product-service:8081/products/{productId}";
        restTemplate.delete(url, productId);
        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange(
                "http://product-service:8081/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        model.addAttribute("allProducts", response.getBody());
        return "products"; // Thymeleaf template (products.html)
    }

    @Override
    public String createRecommendation(Model model, @PathVariable Long productId, @ModelAttribute("recommendation") RecommendationDTO recommendation) {
        String url = "http://recommendation-service:8083/products/{productId}/recommendations";
        restTemplate.postForEntity(url, recommendation, ReviewDTO.class, productId);
        model.addAttribute("product", getUpdatedProduct(productId));
        return "product_item"; // Thymeleaf template (product_item.html)
    }

    @Override
    public String deleteRecommendation(Model model, @PathVariable Long productId, @PathVariable Long recommendationId) {
        String url = "http://recommendation-service:8083/products/{productId}/recommendations/{recommendationId}";
        restTemplate.delete(url, productId, recommendationId);
        model.addAttribute("product", getUpdatedProduct(productId));
        return "product_item"; // Thymeleaf template (product_item.html)
    }

    @Override
    public String createReview(Model model, @PathVariable Long productId, @ModelAttribute("review") ReviewDTO review) {
        String url = "http://review-service:8082/products/{productId}/reviews";
        restTemplate.postForEntity(url, review, ReviewDTO.class, productId);
        model.addAttribute("product", getUpdatedProduct(productId));
        return "product_item"; // Thymeleaf template (product_item.html)
    }

    @Override
    public String deleteReview(Model model, @PathVariable Long productId, @PathVariable Long reviewId) {
        String url = "http://review-service:8082/products/{productId}/reviews/{reviewId}";
        restTemplate.delete(url, productId, reviewId);
        model.addAttribute("product", getUpdatedProduct(productId));
        return "product_item"; // Thymeleaf template (product_item.html)
    }

    public ProductDTO getUpdatedProduct(Long productId) {
        String productUrl = "http://product-service:8081/products/{productId}";
        ResponseEntity<ProductDTO> response = restTemplate.exchange(productUrl, HttpMethod.GET, null, ProductDTO.class, productId);
        return response.getBody();
    }
}
