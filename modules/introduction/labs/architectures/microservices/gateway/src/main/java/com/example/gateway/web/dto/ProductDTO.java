package com.example.gateway.web.dto;

import java.util.List;

public record ProductDTO(Long productId,
                         Integer version,
                         String name,
                         int weight,
                         List<RecommendationDTO> recommendations,
                         List<ReviewDTO> reviews
) {
}
