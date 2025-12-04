package com.nbicocchi.monolith.adapters.api.dto;

public record ReviewDTO(Long reviewId,
                        Long productId,
                        String author,
                        String subject,
                        String content) {
}
