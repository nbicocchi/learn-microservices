package com.nbicocchi.post.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDTO {
    @EqualsAndHashCode.Include private String postUUID;
    @EqualsAndHashCode.Include private String userUUID;
    @EqualsAndHashCode.Include private LocalDateTime timestamp;
    private String content;
}

