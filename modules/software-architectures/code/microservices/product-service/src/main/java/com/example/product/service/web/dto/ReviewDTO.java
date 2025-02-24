package com.example.product.service.web.dto;

public record ReviewDTO(Long reviewId,
                        Long productId,
                        String author,
                        String subject,
                        String content) {
}
