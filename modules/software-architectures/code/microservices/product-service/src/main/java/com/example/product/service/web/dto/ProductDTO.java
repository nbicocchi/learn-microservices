package com.example.product.service.web.dto;

import java.util.*;

public record ProductDTO(Long productId,
                         Integer version,
                         String name,
                         int weight,
                         List<RecommendationDTO> recommendations,
                         List<ReviewDTO> reviews
) {
}
