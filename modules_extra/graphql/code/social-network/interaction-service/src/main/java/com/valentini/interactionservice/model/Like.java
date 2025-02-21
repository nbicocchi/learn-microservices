package com.valentini.interactionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "likes")
public class Like {

        @Id
        @GeneratedValue
        private Long id;

        @Column
        private Long postId;

        @Column
        private Long userId;
}
