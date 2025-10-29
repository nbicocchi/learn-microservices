package com.valentini.interactionservice.resolver;

import com.valentini.interactionservice.exception.BadCredentialsException;
import com.valentini.interactionservice.exception.UserNotFoundException;
import com.valentini.interactionservice.model.Comment;
import com.valentini.interactionservice.model.User;
import com.valentini.interactionservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CommentResolver {
    private final CommentRepository commentRepository;

    @Value("${application.api.key}")
    private String api_key;

    public CommentResolver(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @QueryMapping
    public Iterable<Comment> getCommentsByPostId(@Argument Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @MutationMapping
    public Comment addComment(@Argument Long postId, @Argument Long userId, @Argument String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @MutationMapping
    public Boolean deleteComment(@Argument Long id) {
        commentRepository.deleteById(id);
        return true;
    }

    @SchemaMapping
    public User user(Comment comment) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "{ getUserById(id:" + comment.getUserId() + "){ id username avatarPath} }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new UserNotFoundException(comment.getUserId().toString());
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new UserNotFoundException(comment.getUserId().toString());
            }

            Map<String, Object> userMap = (Map<String, Object>) data.get("getUserById");
            if (userMap != null) {
                User user = new User();
                user.setId(Long.valueOf(userMap.get("id").toString()));
                user.setUsername((String) userMap.get("username"));
                user.setAvatarPath((String) userMap.get("avatarPath"));
                return user;
            } else {
                throw new UserNotFoundException(comment.getUserId().toString());
            }
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new UserNotFoundException(comment.getUserId().toString());
        }
    }
}
