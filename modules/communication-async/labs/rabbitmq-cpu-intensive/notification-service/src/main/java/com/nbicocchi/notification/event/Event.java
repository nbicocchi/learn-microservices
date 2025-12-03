package com.nbicocchi.notification.event;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class Event<K, T> {
    @NonNull private K key;
    @NonNull private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
}