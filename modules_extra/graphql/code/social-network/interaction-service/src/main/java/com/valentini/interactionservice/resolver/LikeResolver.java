package com.valentini.interactionservice.resolver;

import com.valentini.interactionservice.exception.BadCredentialsException;
import com.valentini.interactionservice.exception.UserNotFoundException;
import com.valentini.interactionservice.model.Like;
import com.valentini.interactionservice.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeResolver {
    private final LikeRepository likeRepository;

    @Value("${application.api.key}")
    private String api_key;

    public LikeResolver(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @QueryMapping
    public Iterable<Like> getLikesByPostId(@Argument Long postId) {
        return likeRepository.findByPostId(postId);
    }

    @MutationMapping
    public Like likePost(@Argument Long postId, @Argument Long userId) {
        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        return likeRepository.save(like);
    }

    @MutationMapping
    public Boolean unlikePost(@Argument Long postId, @Argument Long userId) {
        likeRepository.deleteById( likeRepository.findByPostIdAndUserId(postId, userId).getId() );
        return true;
    }

    @QueryMapping
    public Boolean isPostLikedByUser(@Argument Long postId, @Argument String username) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "{ getUserByUsername(username: \"" + username + "\") { id username } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error retrieving data");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error retrieving data");
            }
            if (data.get("getUserByUsername") == null) {
                throw new RuntimeException("Error retrieving data");
            }
            Map<String, Object> userData = (Map<String, Object>) data.get("getUserByUsername");
            return likeRepository.existsByPostIdAndUserId(postId, Long.parseLong(userData.get("id").toString()));
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new UserNotFoundException(username);
        }

    }

}
