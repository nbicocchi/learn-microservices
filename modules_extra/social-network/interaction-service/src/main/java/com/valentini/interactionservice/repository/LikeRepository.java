package com.valentini.interactionservice.repository;

import com.valentini.interactionservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPostId(Long postId);
    List<Like> findByUserId(Long userId);
    Like findByPostIdAndUserId(Long postId, Long userId);
    void deleteById(Long id);
    Boolean existsByPostIdAndUserId(Long postId, Long userId);
}
