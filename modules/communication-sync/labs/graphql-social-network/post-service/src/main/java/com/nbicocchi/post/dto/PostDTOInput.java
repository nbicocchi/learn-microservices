package com.nbicocchi.post.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDTOInput {
    private String userUUID;
    private String timestamp; // keep as String to accept GraphQL ISO-8601
    private String content;
}

