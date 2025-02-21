package com.nbicocchi.monolith.adapters.service;

import com.nbicocchi.monolith.adapters.persistence.implementation.Product;
import com.nbicocchi.monolith.adapters.persistence.implementation.Recommendation;
import com.nbicocchi.monolith.adapters.persistence.implementation.Review;
import com.nbicocchi.monolith.adapters.persistence.repository.IProductCompositeRepository;
import com.nbicocchi.monolith.core.usecase.removal.RemovalOutputBoundary;
import com.nbicocchi.monolith.adapters.api.util.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RemovalService implements RemovalOutputBoundary {

    private final IProductCompositeRepository productRepository;

    public RemovalService(IProductCompositeRepository productRepository) {
        this.productRepository = productRepository;
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
        Recommendation r = productRepository.findRecommendation(recommendationId);
        Optional<Product> p = productRepository.findById(productId);
        if(p.isPresent()){
            Product product = p.get();
            product.removeRecommendation(r);
            productRepository.save(product);
        }else throw new NoSuchElementException();
    }

    @Override
    public void deleteReview(Long productId, Long reviewId) {
        Review r = productRepository.findReview(reviewId);
        Optional<Product> p = productRepository.findById(productId);
        if(p.isPresent()){
            Product product = p.get();
            product.removeReview(r);
            productRepository.save(product);
        }else throw new NoSuchElementException();
    }
}
