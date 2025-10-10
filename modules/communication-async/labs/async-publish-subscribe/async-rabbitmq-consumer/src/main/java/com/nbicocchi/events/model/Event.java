package com.nbicocchi.events.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    @NonNull private Type type;
    @NonNull private K key;
    @NonNull private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
}