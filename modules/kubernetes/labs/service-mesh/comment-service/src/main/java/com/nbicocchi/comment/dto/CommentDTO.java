package com.nbicocchi.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDTO {
    @NonNull @EqualsAndHashCode.Include private String commentUUID;
    @NonNull @EqualsAndHashCode.Include private String postUUID;
    @EqualsAndHashCode.Include private LocalDateTime timestamp;
    private String content;
}

