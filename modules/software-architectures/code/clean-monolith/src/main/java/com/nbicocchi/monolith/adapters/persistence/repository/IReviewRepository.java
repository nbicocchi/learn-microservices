package com.nbicocchi.monolith.adapters.persistence.repository;

import com.nbicocchi.monolith.adapters.persistence.implementation.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface IReviewRepository extends CrudRepository<Review, Long> {
    Set<Review> findReviewsByProductId(Long productId);
}
