package com.nbicocchi.post.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull @EqualsAndHashCode.Include private String userUUID;
    @NonNull @EqualsAndHashCode.Include private LocalDateTime timestamp;
    @NonNull private String content;
}
