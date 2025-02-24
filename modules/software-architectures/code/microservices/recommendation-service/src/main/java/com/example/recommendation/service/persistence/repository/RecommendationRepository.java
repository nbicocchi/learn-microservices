package com.example.recommendation.service.persistence.repository;

import com.example.recommendation.service.persistence.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("SELECT r FROM Recommendation r WHERE r.productId = :productId")
    List<Recommendation> findRecommendationsByProductId(Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Recommendation r WHERE r.recommendationId = :recommendationId")
    void deleteRecommendationById(Long recommendationId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Recommendation r WHERE r.productId = :productId")
    void deleteAllRecommendationsByProductId(Long productId);
}
