package com.example.recommendation.service.web.dto;

public record RecommendationDTO(Long recommendationId,
                                Long productId,
                                Integer version,
                                String author,
                                int rating,
                                String content){
}
