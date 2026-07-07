package com.nbicocchi.bff.dto;

import java.time.LocalDateTime;
import java.util.HashSet;

public record PostOutDTO(LocalDateTime timestamp, String content, HashSet<CommentOutDTO> comments) {
}
