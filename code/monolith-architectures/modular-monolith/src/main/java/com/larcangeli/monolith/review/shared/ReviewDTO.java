package com.larcangeli.monolith.review.shared;

public record ReviewDTO(Long reviewId,
                        Long productId,
                        String author,
                        String subject,
                        String content) {
}
