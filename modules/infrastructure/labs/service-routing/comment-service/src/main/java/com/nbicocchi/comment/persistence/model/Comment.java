package com.nbicocchi.comment.persistence.model;

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
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull @EqualsAndHashCode.Include private String commentUUID;
    @NonNull @EqualsAndHashCode.Include private String postUUID;
    @NonNull @EqualsAndHashCode.Include private LocalDateTime timestamp;
    @NonNull private String content;
}
