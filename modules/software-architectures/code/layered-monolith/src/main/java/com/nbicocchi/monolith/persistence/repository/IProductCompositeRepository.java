package com.nbicocchi.monolith.persistence.repository;

import com.nbicocchi.monolith.persistence.model.Product;
import com.nbicocchi.monolith.persistence.model.Recommendation;
import com.nbicocchi.monolith.persistence.model.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.Set;

public interface IProductCompositeRepository extends CrudRepository<Product, Long> {

    @Query("SELECT r FROM Review r WHERE r.productId = :productId")
    Set<Review> findReviewsByProductId(Long productId);

    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId")
    Review findReview(Long reviewId);

    @Query("SELECT r FROM Recommendation r WHERE r.productId = :productId")
    Set<Recommendation> findRecommendationsByProductId(Long productId);

    @Query("SELECT r FROM Recommendation r WHERE r.recommendationId = :recommendationId")
    Recommendation findRecommendation(Long recommendationId);
}
