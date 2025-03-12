package com.nbicocchi.consumer.model;

import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    @NonNull private Type eventType;
    @NonNull private K key;
    @NonNull private T data;
    private ZonedDateTime eventCreatedAt = ZonedDateTime.now();
}