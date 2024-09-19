package com.larcangeli.monolith.product.shared;

import com.larcangeli.monolith.recommendation.shared.RecommendationDTO;
import com.larcangeli.monolith.review.shared.ReviewDTO;

import java.util.List;

public record ProductDTO(Long productId,
                                  Integer version,
                                  String name,
                                  int weight,
                                  List<RecommendationDTO> recommendations,
                                  List<ReviewDTO> reviews
) {
}
