package com.valentini.postservice.resolver;

import com.valentini.postservice.exception.UserNotFoundException;
import com.valentini.postservice.model.Comment;
import com.valentini.postservice.model.Post;
import com.valentini.postservice.model.User;
import com.valentini.postservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.*;
import com.valentini.postservice.exception.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PostResolver {
    private final PostRepository postRepository;

    @Value("${application.api.key}")
    private String api_key;

    public PostResolver(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @QueryMapping
    public Post getPostById(@Argument Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public Iterable<Post> getPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @MutationMapping
    public Post createPost(@Argument String description, @Argument Long userId, @Argument String imagePath) {
        Post post = new Post();
        post.setDescription(description);
        post.setUserId(userId);
        post.setImagePath(imagePath);
        return postRepository.save(post);
    }

    @MutationMapping
    public Boolean deletePost(@Argument Long id) {
        postRepository.deleteById(id);
        return true;
    }

    @QueryMapping
    public List<Post> getPostsByUserId(@Argument Long userId) {
        return postRepository.findByUserId(userId);
    }

    @SchemaMapping
    public User user(Post post) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "{ getUserById(id:" + post.getUserId() + "){ id username avatarPath } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new UserNotFoundException(post.getUserId().toString());
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new UserNotFoundException(post.getUserId().toString());
            }
            Map<String, Object> userMap = (Map<String, Object>) data.get("getUserById");
            if (userMap != null) {
                User user = new User();
                user.setId(Long.valueOf(userMap.get("id").toString()));
                user.setUsername((String) userMap.get("username"));
                user.setAvatarPath((String) userMap.get("avatarPath"));
                return user;
            } else {
                throw new UserNotFoundException(post.getUserId().toString());
            }
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new UserNotFoundException(post.getUserId().toString());
        }
    }

    @SchemaMapping
    public List<Comment> comments(Post post) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "{ getCommentsByPostId(postId:" + post.getId() + "){ id content user { id username avatarPath } } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error fetching comments for post: " + post.getId().toString());
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error fetching comments for post: " + post.getId().toString());
            }
            List<Object> comments = (List<Object>) data.get("getCommentsByPostId");

            if (comments != null) {
                return comments.stream().map(commentMap -> {
                    Comment comment = new Comment();
                    comment.setId(Long.valueOf(((Map<String, Object>) commentMap).get("id").toString()));
                    comment.setContent((String) ((Map<String, Object>) commentMap).get("content"));
                    User commenter = new User();
                    commenter.setId(Long.valueOf(((Map<String, Object>) ((Map<String, Object>) commentMap).get("user")).get("id").toString()));
                    commenter.setUsername((String) ((Map<String, Object>) ((Map<String, Object>) commentMap).get("user")).get("username"));
                    commenter.setAvatarPath((String) ((Map<String, Object>) ((Map<String, Object>) commentMap).get("user")).get("avatarPath"));
                    comment.setUser(commenter);
                    return comment;
                }).toList();
            } else {
                throw new RuntimeException("Error fetching comments for post: " + post.getId().toString());
            }
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error fetching comments for post: " + post.getId().toString());
        }
    }

    @SchemaMapping
    public Integer likesCount(Post post){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "{ getLikesByPostId(postId:" + post.getId() + "){ id } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error fetching likes count for post: " + post.getId().toString());
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error fetching likes count for post: " + post.getId().toString());
            }
            List<Object> likes = (List<Object>) data.get("getLikesByPostId");

            if (likes != null) {
                return likes.size();
            } else {
                throw new RuntimeException("Error fetching likes count for post: " + post.getId().toString());
            }
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error fetching likes count for post: " + post.getId().toString());
        }
    }

    @QueryMapping
    public Boolean isLikedByUser(@Argument Long postId, @Argument Long userId){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "{ isLikedByUser(postId:" + postId + ", userId:" + userId + ") }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error fetching like status for post: " + postId.toString());
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error fetching like status for post: " + postId.toString());
            }
            return (Boolean) data.get("isLikedByUser");
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error fetching like status for post: " + postId.toString());
        }
    }


}
