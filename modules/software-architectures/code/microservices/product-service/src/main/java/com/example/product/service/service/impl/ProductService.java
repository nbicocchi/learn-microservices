package com.example.product.service.service.impl;

import com.example.product.service.mapper.ProductMapper;
import com.example.product.service.persistence.model.Product;
import com.example.product.service.persistence.repository.ProductRepository;
import com.example.product.service.service.IProductService;
import com.example.product.service.web.dto.ProductDTO;
import com.example.product.service.web.dto.RecommendationDTO;
import com.example.product.service.web.dto.ReviewDTO;
import com.example.product.service.web.exceptions.InvalidInputException;
import com.example.product.service.web.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class ProductService implements IProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repo;
    private final ProductMapper mapper;
    private final WebClient webClient;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, WebClient.Builder webClientBuilder) {
        this.repo = productRepository;
        this.mapper = productMapper;
        this.webClient = webClientBuilder.build();
    }

    public ProductDTO findById(Long id){
        if (id < 1) {
            throw new InvalidInputException("Invalid productId: " + id);
        }

        Product entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("No product found with ID: " + id));
        ProductDTO productDTO = mapper.toDTO(entity);

         List<ReviewDTO> reviews = getReviews(entity.getProductId());
         List<RecommendationDTO> recommendations = getRecommendations(entity.getProductId());

        return new ProductDTO(
                productDTO.productId(),
                productDTO.version(),
                productDTO.name(),
                productDTO.weight(),
                recommendations != null ? recommendations : List.of(),
                reviews != null ? reviews : List.of()
        );
    }


    public Collection<ProductDTO> findAll(){
        List<ProductDTO> products = new ArrayList<>();
        repo.findAll().forEach(product -> {
            ProductDTO baseDTO = mapper.toDTO(product);

            List<ReviewDTO> reviews = getReviews(product.getProductId());
            List<RecommendationDTO> recommendations = getRecommendations(product.getProductId());

            products.add(new ProductDTO(
                    baseDTO.productId(),
                    baseDTO.version(),
                    baseDTO.name(),
                    baseDTO.weight(),
                    recommendations,
                    reviews
            ));
        });
        return products;
    }


    public ProductDTO save(ProductDTO product) {
        try {
            Product savedProduct = repo.save(mapper.toEntity(product));
            // create reviews and recommendation
            createReviews(product.reviews());
            createRecommendations(product.recommendations());
            return mapper.toDTO(savedProduct);
        }catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    public void deleteById(Long id) {
        Optional<Product> productOpt = repo.findById(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        deleteReviews(id);
        deleteRecommendations(id);

        repo.deleteProductById(id);
        repo.flush();
    }

    private List<ReviewDTO> getReviews(Long productId) {
        return webClient.get()
                .uri("http://review-service:8082/products/{productId}/reviews", productId)
                .retrieve()
                .bodyToFlux(ReviewDTO.class)
                .collectList()
                .block();
    }

    private List<RecommendationDTO> getRecommendations(Long productId) {
        return webClient.get()
                .uri("http://recommendation-service:8083/products/{productId}/recommendations", productId)
                .retrieve()
                .bodyToFlux(RecommendationDTO.class)
                .collectList()
                .block();
    }

    private void createReviews(List<ReviewDTO> reviews) {
        if (reviews != null && !reviews.isEmpty()) {
            webClient.post()
                    .uri("http://review-service:8082/products/{productId}/reviews")
                    .body(Mono.just(reviews), List.class)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }

    private void createRecommendations(List<RecommendationDTO> recommendations) {
        if (recommendations != null && !recommendations.isEmpty()) {
            webClient.post()
                    .uri("http:///recommendation-service:8083/products/{productId}/recommendations")
                    .body(Mono.just(recommendations), List.class)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }

    private void deleteReviews(Long productId) {
        webClient.delete()
                .uri("http://review-service:8082/products/{productId}/reviews", productId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void deleteRecommendations(Long productId) {
        webClient.delete()
                .uri("http://recommendation-service:8083/products/{productId}/recommendations", productId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
