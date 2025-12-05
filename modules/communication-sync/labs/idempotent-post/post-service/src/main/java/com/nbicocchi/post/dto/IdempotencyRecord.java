package com.nbicocchi.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord<T> {
    private String key;
    private T responseBody;
    private int responseStatus;
}

