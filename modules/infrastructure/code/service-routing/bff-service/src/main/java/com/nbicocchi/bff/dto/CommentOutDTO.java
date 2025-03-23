package com.nbicocchi.bff.dto;

import java.time.LocalDateTime;

public record CommentOutDTO(LocalDateTime timestamp, String content) {
}
