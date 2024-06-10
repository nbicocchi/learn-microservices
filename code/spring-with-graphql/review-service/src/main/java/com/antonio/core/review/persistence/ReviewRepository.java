package com.antonio.core.review.persistence;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<ReviewEntity, String> {
    List<ReviewEntity> findByProductId(int productId);
}