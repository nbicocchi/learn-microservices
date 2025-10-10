package com.nbicocchi.monolith.adapters.service;

import com.nbicocchi.monolith.adapters.persistence.implementation.Product;
import com.nbicocchi.monolith.adapters.persistence.implementation.Recommendation;
import com.nbicocchi.monolith.adapters.persistence.implementation.Review;
import com.nbicocchi.monolith.adapters.persistence.repository.IProductRepository;
import com.nbicocchi.monolith.adapters.persistence.repository.IRecommendationRepository;
import com.nbicocchi.monolith.adapters.persistence.repository.IReviewRepository;
import com.nbicocchi.monolith.core.usecase.removal.RemovalOutputBoundary;
import com.nbicocchi.monolith.adapters.api.util.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RemovalService implements RemovalOutputBoundary {

    private final IProductRepository productRepository;
    private final IReviewRepository reviewRepository;

    private final IRecommendationRepository recommendationRepository;

    public RemovalService(IProductRepository productRepository, IReviewRepository reviewRepository, IRecommendationRepository recommendationRepository) {

        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public void deleteProduct(Long productId) {
        Optional<Product> p = productRepository.findById(productId);
        if(p.isEmpty())
            throw new NotFoundException();
        productRepository.deleteById(productId);
    }

    @Override
    public void deleteRecommendation(Long productId, Long recommendationId) {
        Optional<Recommendation> r = recommendationRepository.findById(recommendationId);
        Optional<Product> p = productRepository.findById(productId);
        if(p.isPresent() && r.isPresent()){
            Product product = p.get();
            Recommendation recommendation = r.get();
            product.removeRecommendation(recommendation);
            productRepository.save(product);
        }else throw new NoSuchElementException();
    }

    @Override
    public void deleteReview(Long productId, Long reviewId) {
        Optional<Review> r = reviewRepository.findById(reviewId);
        Optional<Product> p = productRepository.findById(productId);
        if(p.isPresent() && r.isPresent()){
            Product product = p.get();
            Review review = r.get();
            product.removeReview(review);
            productRepository.save(product);
        }else throw new NoSuchElementException();
    }
}
