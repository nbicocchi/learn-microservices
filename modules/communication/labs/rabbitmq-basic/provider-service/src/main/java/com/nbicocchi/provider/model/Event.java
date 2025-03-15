package com.nbicocchi.provider.model;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}

    @NonNull private Type type;
    @NonNull private K key;
    @NonNull private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
}