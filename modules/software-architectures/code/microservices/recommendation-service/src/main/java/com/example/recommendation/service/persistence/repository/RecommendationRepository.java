package com.example.recommendation.service.persistence.repository;

import com.example.recommendation.service.persistence.model.Recommendation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecommendationRepository extends CrudRepository<Recommendation, Long> {

    List<Recommendation> findRecommendationsByProductId(Long productId);
}
