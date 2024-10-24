package com.nbicocchi.monolith.recommendation.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface RecommendationRepository extends CrudRepository<Recommendation, Long> {

    @Query("SELECT r FROM Recommendation r WHERE r.productId = :productId")
    List<Recommendation> findRecommendationsByProductId(Long productId);
}
