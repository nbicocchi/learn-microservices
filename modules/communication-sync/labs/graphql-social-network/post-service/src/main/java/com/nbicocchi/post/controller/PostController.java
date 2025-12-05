package com.nbicocchi.post.controller;

import com.nbicocchi.post.dto.PostDTO;
import com.nbicocchi.post.dto.PostDTOInput;
import com.nbicocchi.post.persistence.model.Post;
import com.nbicocchi.post.persistence.repository.PostRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.time.LocalDateTime;

@Controller
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // -------------------------------
    // Queries
    // -------------------------------
    @QueryMapping
    public List<PostDTO> allPosts() {
        return StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<PostDTO> postsByUser(@Argument String userUUID) {
        return StreamSupport.stream(postRepository.findByUserUUID(userUUID).spliterator(), false)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------
    // Mutations
    // -------------------------------
    @MutationMapping
    public PostDTO addPost(@Argument("input") PostDTOInput input) {
        if (input.getUserUUID() == null || input.getContent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }

        LocalDateTime timestamp = input.getTimestamp() != null
                ? LocalDateTime.parse(input.getTimestamp())
                : LocalDateTime.now();

        // Map input to Post entity
        Post post = new Post(input.getUserUUID(), timestamp, input.getContent());
        Post saved = postRepository.save(post);

        // Return PostDTO as output
        return new PostDTO(saved.getUserUUID(), saved.getTimestamp(), saved.getContent());
    }


    @MutationMapping
    public Boolean removePost(@Argument String userUUID, @Argument String timestamp) {
        LocalDateTime ts = LocalDateTime.parse(timestamp);
        List<Post> posts = postRepository.findByUserUUIDAndTimestamp(userUUID, ts);
        if (posts.isEmpty()) {
            return false;
        }
        postRepository.deleteAll(posts);
        return true;
    }

    // -------------------------------
    // Mapping helpers
    // -------------------------------
    private PostDTO mapToDTO(Post post) {
        return new PostDTO(
                post.getUserUUID(),
                post.getTimestamp(),
                post.getContent()
        );
    }

    private Post mapToModel(PostDTO dto) {
        return new Post(
                dto.getUserUUID(),
                dto.getTimestamp(),
                dto.getContent()
        );
    }
}
