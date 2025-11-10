package com.nbicocchi.user.integration;


import com.nbicocchi.user.dto.PostDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class PostIntegration {
    String postServiceHost;
    int postServicePort;

    public PostIntegration(
            @Value("${app.post-service.host}") String postServiceHost,
            @Value("${app.post-service.port}") int postServicePort) {
        this.postServiceHost = postServiceHost;
        this.postServicePort = postServicePort;
    }

    public Iterable<PostDTO> findbyUserUUID(String userUUID) {
        String url = "http://" + postServiceHost + ":" + postServicePort + "/graphql";
        RestClient restClient = RestClient.builder().build();

        String query = """
            query($userUUID: String!) {
              postsByUser(userUUID: $userUUID) {
                userUUID
                timestamp
                content
              }
            }
            """;

        Map<String, Object> requestBody = Map.of(
                "query", query,
                "variables", Map.of("userUUID", userUUID)
        );

        System.out.println("helllo");
        Map<String, Object> response = restClient
                .post()
                .uri(url)
                .body(requestBody)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        System.out.println(response);

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        List<Map<String, Object>> posts = (List<Map<String, Object>>) data.get("postsByUser");

        List<PostDTO> postDTOList = posts.stream()
                .map(p -> new PostDTO(
                        (String) p.get("userUUID"),
                        LocalDateTime.parse((String) p.get("timestamp")),
                        (String) p.get("content")
                )).toList();

        return postDTOList; // List<PostDTO> is Iterable<PostDTO>
    }
}
