package com.nbicocchi.monolith.persistence.repository;



import com.nbicocchi.monolith.persistence.model.Recommendation;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface IRecommendationRepository extends CrudRepository<Recommendation, Long> {
    Set<Recommendation> findRecommendationsByProductId(Long productId);
}
