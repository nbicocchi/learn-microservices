package com.nbicocchi.monolith.review.domain;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ReviewRepository extends CrudRepository<Review, Long> {
    List<Review> findRecommendationsByProductId(Long productId);

}
