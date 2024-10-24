package com.nbicocchi.monolith.service;

import com.nbicocchi.monolith.persistence.model.Product;
import com.nbicocchi.monolith.persistence.model.Recommendation;
import com.nbicocchi.monolith.persistence.model.Review;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IProductCompositeService {
    Optional<Product> findById(Long id);

    Collection<Product> findAll();

    Product save(Product product);

    void deleteById(Long id);

    void saveRecommendation(Recommendation recommendation);

    void saveReview(Review review);

    void deleteRecommendation(Long productId, Long recommendationId);

    void deleteReview(Long productId, Long reviewId);

    Set<Recommendation> findRecommendationsByProductId(Long productId);

    Set<Review> findReviewsByProductId(Long productId);
}
