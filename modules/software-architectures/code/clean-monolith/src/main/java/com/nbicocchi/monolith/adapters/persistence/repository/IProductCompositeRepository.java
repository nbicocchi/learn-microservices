package com.nbicocchi.monolith.adapters.persistence.repository;

import com.nbicocchi.monolith.adapters.persistence.implementation.Product;
import com.nbicocchi.monolith.adapters.persistence.implementation.Recommendation;
import com.nbicocchi.monolith.adapters.persistence.implementation.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface IProductCompositeRepository extends CrudRepository<Product, Long> {

    List<Product> findAll();

    @Query("SELECT r FROM Review r WHERE r.productId = :productId")
    Set<Review> findReviewsByProductId(Long productId);

    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId")
    Review findReview(Long reviewId);

    @Query("SELECT r FROM Recommendation r WHERE r.productId = :productId")
    Set<Recommendation> findRecommendationsByProductId(Long productId);

    @Query("SELECT r FROM Recommendation r WHERE r.recommendationId = :recommendationId")
    Recommendation findRecommendation(Long recommendationId);


}
