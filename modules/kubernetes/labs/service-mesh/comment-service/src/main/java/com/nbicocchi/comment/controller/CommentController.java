package com.nbicocchi.comment.controller;

import com.nbicocchi.comment.dto.CommentDTO;
import com.nbicocchi.comment.persistence.model.Comment;
import com.nbicocchi.comment.persistence.repository.CommentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/comments")
public class CommentController {
    CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping("/{postUUID}")
    public Iterable<CommentDTO> findByUuid(@PathVariable String postUUID) {
        Iterable<Comment> foundComments = commentRepository.findByPostUUID(postUUID);
        return mapToDTO(foundComments);
    }

    @GetMapping
    public Iterable<CommentDTO> findAll() {
        Iterable<Comment> foundComments = commentRepository.findAll();
        return mapToDTO(foundComments);
    }

    private Iterable<CommentDTO> mapToDTO(Iterable<Comment> comments) {
        return StreamSupport.stream(comments.spliterator(), false)
                .map(c -> new CommentDTO(c.getCommentUUID(), c.getPostUUID(), c.getTimestamp(), c.getContent()))
                .collect(Collectors.toList());
    }
}