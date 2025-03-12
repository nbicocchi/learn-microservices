package com.nbicocchi.post.controller;

import com.nbicocchi.post.controller.dto.PostDTO;
import com.nbicocchi.post.persistence.model.Post;
import com.nbicocchi.post.persistence.repository.PostRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/posts")
public class PostController {
    PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/{userUUID}")
    public Iterable<PostDTO> findByUuid(@PathVariable String userUUID) {
        Iterable<Post> foundPosts = postRepository.findByUserUUID(userUUID);
        return mapToDTO(foundPosts);
    }

    @GetMapping
    public Iterable<PostDTO> findAll() {
        Iterable<Post> foundPosts = postRepository.findAll();
        return mapToDTO(foundPosts);
    }

    private Iterable<PostDTO> mapToDTO(Iterable<Post> posts) {
        return StreamSupport.stream(posts.spliterator(), false)
                .map(p -> new PostDTO(p.getUserUUID(), p.getTimestamp(), p.getContent()))
                .collect(Collectors.toList());
    }
}