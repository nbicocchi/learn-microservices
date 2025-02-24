package com.example.review.service.persistence.repository;

import com.example.review.service.persistence.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.productId = :productId")
    List<Review> findByProductId(Long productId);
    @Transactional
    @Modifying
    @Query("DELETE FROM Review r WHERE r.reviewId = :reviewId")
    void deleteReviewById(Long reviewId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Review r WHERE r.productId = :productId")
    void deleteAllReviewsByProductId(Long productId);
}
