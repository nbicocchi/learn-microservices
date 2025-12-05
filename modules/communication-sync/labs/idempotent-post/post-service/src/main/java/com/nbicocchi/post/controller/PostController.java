package com.nbicocchi.post.controller;

import com.nbicocchi.post.controller.dto.PostDTO;
import com.nbicocchi.post.persistence.model.Post;
import com.nbicocchi.post.persistence.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepository;
    RedisTemplate<String, String> redisTemplate;

    public PostController(PostRepository postRepository,
                          RedisTemplate<String, String> redisTemplate) {
        this.postRepository = postRepository;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public Iterable<PostDTO> findAll() {
        Iterable<Post> foundPosts = postRepository.findAll();
        return mapToDTO(foundPosts);
    }

    @GetMapping("/{userUUID}")
    public Iterable<PostDTO> findByUuid(@PathVariable String userUUID) {
        Iterable<Post> foundPosts = postRepository.findByUserUUID(userUUID);
        return mapToDTO(foundPosts);
    }

    @PostMapping
    public ResponseEntity<PostDTO> addPost(
            @RequestHeader("Idempotency-Key") String key,
            @RequestBody PostDTO postDTO) {

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        // Try to set the key only if it does not exist
        Boolean success = ops.setIfAbsent(key, "1", Duration.ofHours(1));
        if (Boolean.FALSE.equals(success)) {
            // Key already exists → duplicate request
            log.info("Key {} exists", key);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Save post in DB
        Post post = new Post(postDTO.getUserUUID(), postDTO.getTimestamp(), postDTO.getContent());
        log.info("Adding post: {}", post);
        postRepository.save(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    private Iterable<PostDTO> mapToDTO(Iterable<Post> posts) {
        return StreamSupport.stream(posts.spliterator(), false)
                .map(p -> new PostDTO(p.getUserUUID(), p.getTimestamp(), p.getContent()))
                .collect(Collectors.toList());
    }
}
