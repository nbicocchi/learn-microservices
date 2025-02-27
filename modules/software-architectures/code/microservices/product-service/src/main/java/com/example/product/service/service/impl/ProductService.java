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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class ProductService implements IProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repo;
    private final ProductMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.repo = productRepository;
        this.mapper = productMapper;
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
            createReviews(product.productId(), product.reviews());
            createRecommendations(product.productId(), product.recommendations());
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

        repo.deleteById(id);
    }

    private List<ReviewDTO> getReviews(Long productId) {
        String url = "http://review-service:8082/products/{productId}/reviews";
        ResponseEntity<List<ReviewDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                productId
        );
        return response.getBody();
    }

    private List<RecommendationDTO> getRecommendations(Long productId) {
        String url = "http://recommendation-service:8083/products/{productId}/recommendations";
        ResponseEntity<List<RecommendationDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                productId
        );
        return response.getBody();
    }

    private void createReviews(Long productId, List<ReviewDTO> reviews) {
        if (reviews != null && !reviews.isEmpty()) {
            String url = "http://review-service:8082/products/{productId}/reviews";
            HttpEntity<List<ReviewDTO>> requestEntity = new HttpEntity<>(reviews);
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class,
                    productId
            );
        }
    }

    private void createRecommendations(Long productId, List<RecommendationDTO> recommendations) {
        if (recommendations != null && !recommendations.isEmpty()) {
            String url = "http://recommendation-service:8083/products/{productId}/recommendations";
            HttpEntity<List<RecommendationDTO>> requestEntity = new HttpEntity<>(recommendations);
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class,
                    productId
            );
        }
    }

    private void deleteReviews(Long productId) {
        String url = "http://review-service:8082/products/{productId}/reviews";
        restTemplate.delete(url, productId);
    }

    private void deleteRecommendations(Long productId) {
        String url = "http://recommendation-service:8083/products/{productId}/recommendations";
        restTemplate.delete(url, productId);
    }
}
