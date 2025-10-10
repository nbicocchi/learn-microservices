package com.nbicocchi.post.persistence.repository;

import com.nbicocchi.post.persistence.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    Iterable<Post> findByUserUUID(String userUUID);
}
