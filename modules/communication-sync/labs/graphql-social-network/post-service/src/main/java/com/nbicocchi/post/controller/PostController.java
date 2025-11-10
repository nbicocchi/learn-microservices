package com.nbicocchi.post.controller;

import com.nbicocchi.post.persistence.model.Post;
import com.nbicocchi.post.persistence.repository.PostRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * query {
 *   allPosts {
 *     userUUID
 *     timestamp
 *     content
 *   }
 * }
 *
 * query {
 *   postsByUser(userUUID: "171f5df0-b213-4a40-8ae6-fe82239ab660") {
 *     userUUID
 *     timestamp
 *     content
 *   }
 * }
 */


@Controller
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @QueryMapping
    public List<Post> allPosts() {
        return StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<Post> postsByUser(@Argument String userUUID) {
        return StreamSupport.stream(postRepository.findByUserUUID(userUUID).spliterator(), false)
                .collect(Collectors.toList());
    }
}
