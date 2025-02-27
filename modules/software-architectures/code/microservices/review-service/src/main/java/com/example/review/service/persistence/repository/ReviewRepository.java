package com.example.review.service.persistence.repository;

import com.example.review.service.persistence.model.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    List<Review> findByProductId(Long productId);
}
