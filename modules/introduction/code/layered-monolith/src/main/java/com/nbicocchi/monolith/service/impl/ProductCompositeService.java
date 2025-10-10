package com.nbicocchi.monolith.service.impl;

import com.nbicocchi.monolith.persistence.model.Product;
import com.nbicocchi.monolith.persistence.model.Recommendation;
import com.nbicocchi.monolith.persistence.model.Review;
import com.nbicocchi.monolith.persistence.repository.IProductRepository;
import com.nbicocchi.monolith.persistence.repository.IRecommendationRepository;
import com.nbicocchi.monolith.persistence.repository.IReviewRepository;
import com.nbicocchi.monolith.service.IProductCompositeService;
import com.nbicocchi.monolith.web.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductCompositeService implements IProductCompositeService {

    private final IProductRepository productRepository;
    private final IRecommendationRepository recommendationRepository;
    private final IReviewRepository reviewRepository;

    public ProductCompositeService(IProductRepository productRepository, IRecommendationRepository recommendationRepository, IReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.recommendationRepository= recommendationRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    @Override
    public Collection<Product> findAll(){
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        return products;
    }
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        Optional<Product> p = productRepository.findById(id);
        if(p.isEmpty())
            throw new NotFoundException();
        productRepository.deleteById(id);
    }

    @Override
    public void saveRecommendation(Recommendation recommendation) {
        Optional<Product> p = productRepository.findById(recommendation.getProductId());
        if(p.isPresent()){
            Product product = p.get();
            product.addRecommendation(recommendation);
            productRepository.save(product);
        }else throw new NoSuchElementException();
    }

    @Override
    public void saveReview(Review review){
        Optional<Product> p = productRepository.findById(review.getProductId());
        if(p.isPresent()){
            Product product = p.get();
            product.addReview(review);
            productRepository.save(product);
        }else throw new NoSuchElementException();
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

    @Override
    public Set<Recommendation> findRecommendationsByProductId(Long productId) {
        if(productRepository.findById(productId).isPresent()){
            return recommendationRepository.findRecommendationsByProductId(productId);
        }else throw new NoSuchElementException();
    }

    @Override
    public Set<Review> findReviewsByProductId(Long productId) {
        if(productRepository.findById(productId).isPresent()){
            return reviewRepository.findReviewsByProductId(productId);
        }else throw new NoSuchElementException();
    }
}
