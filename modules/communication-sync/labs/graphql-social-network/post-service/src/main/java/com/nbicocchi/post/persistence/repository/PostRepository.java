package com.nbicocchi.post.persistence.repository;

import com.nbicocchi.post.persistence.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    // Find all posts by a given user
    Iterable<Post> findByUserUUID(String userUUID);

    // Find posts by user and timestamp (for deletion)
    List<Post> findByUserUUIDAndTimestamp(String userUUID, LocalDateTime timestamp);
}
