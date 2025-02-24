package com.example.gateway.web.controller.impl;

import com.example.gateway.web.controller.IUIController;
import com.example.gateway.web.dto.ProductDTO;
import com.example.gateway.web.dto.RecommendationDTO;
import com.example.gateway.web.dto.ReviewDTO;
import com.example.gateway.web.exceptions.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
public class UIController implements IUIController {
    private final WebClient webClient;

    @Autowired
    public UIController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String homePage(Model model) {
        model.addAttribute("message", "Hello from Thymeleaf!");
        return "home";
    }

    @Override
    public Mono<String> getAllProducts(Model model) {
        return webClient.get()
                .uri("http://product-service:8081/products")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .doOnNext(products -> model.addAttribute("allProducts", products))
                .then(Mono.just("products")); // Thymeleaf template (products.html)
    }

    @Override
    public Mono<String> getProduct(Model model, @PathVariable Long productId){
        if(productId < 0){
            throw new UnprocessableEntityException("Invalid productId: " + productId);
        }
        return webClient.get()
                .uri("http://product-service:8081/products/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .doOnNext(product -> model.addAttribute("product", product))
                .then(Mono.just("product_item")); // Thymeleaf template (product_item.html)
    }

    @Override
    public Mono<String> createProduct(Model model, @ModelAttribute("product") ProductDTO request){
        return webClient.post()
                .uri("http://product-service:8081/products")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .thenMany(webClient.get()
                        .uri("http://product-service:8081/products")
                        .retrieve()
                        .bodyToFlux(ProductDTO.class)
                )
                .collectList()
                .doOnSuccess(products -> model.addAttribute("allProducts", products))
                .thenReturn("products");
    }

    @Override
    public Mono<String> deleteProduct(Model model, @PathVariable Long productId){
        return webClient.delete()
                .uri("http://product-service:8081/products/{productId}", productId)
                .retrieve()
                .bodyToMono(Void.class)
                .thenMany(webClient.get()
                        .uri("http://product-service:8081/products")
                        .retrieve()
                        .bodyToFlux(ProductDTO.class)
                )
                .collectList()
                .doOnSuccess(products -> model.addAttribute("allProducts", products))
                .thenReturn("products");
    }

    @Override
    public Mono<String> createRecommendation(Model model, @PathVariable Long productId, @ModelAttribute("recommendation") RecommendationDTO recommendation){
        return webClient.post()
                .uri("http://recommendation-service:8083/products/{productId}/recommendations", productId)
                .bodyValue(recommendation)
                .retrieve()
                .bodyToMono(RecommendationDTO.class)
                .flatMap(createdRec ->
                        webClient.get()
                                .uri("http://product-service:8081/products/{productId}", productId)
                                .retrieve()
                                .bodyToMono(ProductDTO.class)
                )
                .doOnNext(updatedProduct -> model.addAttribute("product", updatedProduct))
                .thenReturn("product_item");
    }

    @Override
    public Mono<String> deleteRecommendation(Model model, @PathVariable Long productId, @PathVariable Long recommendationId){
        return webClient.delete()
                .uri("http://recommendation-service:8083/products/{productId}/recommendations/{recommendationId}", productId, recommendationId)
                .retrieve()
                .bodyToMono(Void.class)
                .then(
                        webClient.get()
                                .uri("http://product-service:8081/products/{productId}", productId)
                                .retrieve()
                                .bodyToMono(ProductDTO.class)
                )
                .doOnNext(updatedProduct -> model.addAttribute("product", updatedProduct))
                .thenReturn("product_item");
    }

    @Override
    public Mono<String> createReview(Model model, @PathVariable Long productId, @ModelAttribute("review") ReviewDTO review){
        return webClient.post()
                .uri("http://review-service:8082/products/{productId}/reviews", productId)
                .bodyValue(review)
                .retrieve()
                .bodyToMono(ReviewDTO.class)
                .flatMap(createdReview ->
                        webClient.get()
                                .uri("http://product-service:8081/products/{productId}", productId)
                                .retrieve()
                                .bodyToMono(ProductDTO.class)
                )
                .doOnNext(updatedProduct -> model.addAttribute("product", updatedProduct))
                .thenReturn("product_item");
    }

    @Override
    public Mono<String> deleteReview(Model model, @PathVariable Long productId, @PathVariable Long reviewId){
        return webClient.delete()
                .uri("http://review-service:8082/products/{productId}/reviews/{reviewId}", productId, reviewId)
                .retrieve()
                .bodyToMono(Void.class)
                .then(
                        webClient.get()
                                .uri("http://product-service:8081/products/{productId}", productId)
                                .retrieve()
                                .bodyToMono(ProductDTO.class)
                )
                .doOnNext(updatedProduct -> model.addAttribute("product", updatedProduct))
                .thenReturn("product_item");
    }
}
