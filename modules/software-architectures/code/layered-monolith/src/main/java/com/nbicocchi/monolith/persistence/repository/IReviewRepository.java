package com.nbicocchi.monolith.persistence.repository;


import com.nbicocchi.monolith.persistence.model.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface IReviewRepository extends CrudRepository<Review, Long> {
    Set<Review> findReviewsByProductId(Long productId);
}
