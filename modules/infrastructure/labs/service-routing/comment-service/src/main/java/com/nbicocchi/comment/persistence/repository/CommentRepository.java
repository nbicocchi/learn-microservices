package com.nbicocchi.comment.persistence.repository;

import com.nbicocchi.comment.persistence.model.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    Iterable<Comment> findByPostUUID(String userUUID);
}
