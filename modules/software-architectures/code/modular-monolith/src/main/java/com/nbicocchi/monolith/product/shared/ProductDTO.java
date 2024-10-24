package com.nbicocchi.monolith.product.shared;

import com.nbicocchi.monolith.recommendation.shared.RecommendationDTO;
import com.nbicocchi.monolith.review.shared.ReviewDTO;

import java.util.List;

public record ProductDTO(Long productId,
                                  Integer version,
                                  String name,
                                  int weight,
                                  List<RecommendationDTO> recommendations,
                                  List<ReviewDTO> reviews
) {
}
