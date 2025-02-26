package com.nbicocchi.monolith.adapters.persistence.repository;

import com.nbicocchi.monolith.adapters.persistence.implementation.Recommendation;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface IRecommendationRepository extends CrudRepository<Recommendation, Long> {
    Set<Recommendation> findRecommendationsByProductId(Long productId);
}
