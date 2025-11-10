package com.nbicocchi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDTO {
    @EqualsAndHashCode.Include private String userUUID;
    @EqualsAndHashCode.Include private LocalDateTime timestamp;
    private String content;
}

