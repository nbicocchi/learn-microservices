package com.example.gateway.web.dto;

public record RecommendationDTO(Long recommendationId,
                                Long productId,
                                Integer version,
                                String author,
                                int rating,
                                String content){
}
