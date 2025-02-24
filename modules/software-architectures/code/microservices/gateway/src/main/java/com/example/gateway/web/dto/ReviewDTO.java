package com.example.gateway.web.dto;

public record ReviewDTO(Long reviewId,
                        Long productId,
                        String author,
                        String subject,
                        String content) {
}
